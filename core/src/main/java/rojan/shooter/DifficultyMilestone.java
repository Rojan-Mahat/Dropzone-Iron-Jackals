package rojan.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Optional one-shot overlay player. Uses your already-begun HUD SpriteBatch.
 * Owns only its atlas; does not pause gameplay, consume input, or change screens.
 * Construct, draw and dispose on the render thread.
 */
public final class DifficultyMilestone implements Disposable {
    private static final int STRIDE = 7;
    private static final double FRAME_EPSILON = 0.00001;
    private final TextureAtlas atlas;
    private final TextureRegion[] regions;
    private final ObjectMap<String, float[][]> variants = new ObjectMap<String, float[][]>();
    private final float[][] titleTransforms;
    private final double fps, duration;
    private final int frameCount;
    private final float cropWidth, cropHeight;
    private final float defaultX, defaultY;
    private float[][] activeFrames;
    private String variant;
    private double elapsed;
    private boolean disposed;

    /** internalDirectory example: "ui/iron_jackals_difficulty_milestones". */
    public DifficultyMilestone(String internalDirectory, boolean use2x) {
        FileHandle directory = Gdx.files.internal(internalDirectory);
        JsonValue manifest = new JsonReader().parse(directory.child("playback.json"));
        fps = positive(manifest.getDouble("fps"), "fps");
        duration = positive(manifest.getDouble("duration"), "duration");
        frameCount = manifest.getInt("frame_count");
        if (frameCount <= 0 || Math.abs(frameCount - duration * fps) > 0.0001)
            throw new IllegalArgumentException("Frame count, fps and duration disagree");
        if (manifest.getBoolean("loop", false))
            throw new IllegalArgumentException("Milestone playback must not loop");
        JsonValue crop = required(manifest, "crop");
        cropWidth = (float) positive(crop.getDouble(0), "crop width");
        cropHeight = (float) positive(crop.getDouble(1), "crop height");
        JsonValue placement = required(manifest, "placement");
        defaultX = finiteValue(placement.getFloat("x"), "placement x");
        defaultY = finiteValue(placement.getFloat("y_bottom"), "placement y_bottom");

        JsonValue names = required(manifest, "regions");
        if (names.size == 0) throw new IllegalArgumentException("Empty regions");
        JsonValue allVariants = required(manifest, "variants");
        for (JsonValue item = allVariants.child; item != null; item = item.next) {
            JsonValue frameList = required(item, "frames");
            if (frameList.size != frameCount)
                throw new IllegalArgumentException("Wrong frame count for " + item.name);
            float[][] frames = new float[frameCount][];
            int index = 0;
            for (JsonValue frame = frameList.child; frame != null; frame = frame.next) {
                float[] commands = new float[frame.size * STRIDE];
                int offset = 0;
                for (JsonValue command = frame.child; command != null; command = command.next) {
                    if (command.size != STRIDE)
                        throw new IllegalArgumentException("Draw command requires 7 values");
                    for (int n = 0; n < STRIDE; n++)
                        commands[offset + n] = finiteValue(command.getFloat(n), "draw command");
                    float id = commands[offset];
                    if (id < 0 || id >= names.size || id != (int) id)
                        throw new IllegalArgumentException("Invalid region index");
                    positive(commands[offset + 3], "draw width");
                    positive(commands[offset + 4], "draw height");
                    alpha(commands[offset + 6]);
                    offset += STRIDE;
                }
                frames[index++] = commands;
            }
            variants.put(item.name, frames);
        }
        if (variants.size == 0) throw new IllegalArgumentException("Empty variants");

        JsonValue transformList = required(manifest, "title_transform");
        if (transformList.size != frameCount)
            throw new IllegalArgumentException("Wrong title_transform frame count");
        titleTransforms = new float[frameCount][5];
        int transformIndex = 0;
        for (JsonValue transform = transformList.child; transform != null; transform = transform.next) {
            if (transform.size != 5)
                throw new IllegalArgumentException("Title transform requires 5 values");
            for (int n = 0; n < 5; n++)
                titleTransforms[transformIndex][n] = finiteValue(transform.getFloat(n), "title transform");
            positive(titleTransforms[transformIndex][2], "title width");
            positive(titleTransforms[transformIndex][3], "title height");
            alpha(titleTransforms[transformIndex][4]);
            transformIndex++;
        }

        TextureAtlas loaded = new TextureAtlas(directory.child(
                manifest.getString(use2x ? "atlas_2x" : "atlas_1x")));
        try {
            regions = new TextureRegion[names.size];
            for (int n = 0; n < regions.length; n++) {
                TextureAtlas.AtlasRegion region = loaded.findRegion(names.getString(n));
                if (region == null) throw new IllegalArgumentException("Missing region " + names.getString(n));
                if (region.rotate || region.offsetX != 0 || region.offsetY != 0
                        || region.originalWidth != region.packedWidth
                        || region.originalHeight != region.packedHeight)
                    throw new IllegalArgumentException("Expected untrimmed, unrotated atlas region");
                regions[n] = region;
            }
        } catch (RuntimeException error) {
            loaded.dispose();
            throw error;
        }
        atlas = loaded;
    }

    /** Play or replace the active one-shot. Call on an event, never every render. */
    public void play(String variantName) {
        requireAlive();
        float[][] found = variants.get(variantName);
        if (found == null) throw new IllegalArgumentException("Unknown milestone variant: " + variantName);
        activeFrames = found;
        variant = variantName;
        elapsed = 0;
    }

    public void update(float deltaSeconds) {
        requireAlive();
        if (Float.isNaN(deltaSeconds) || Float.isInfinite(deltaSeconds) || deltaSeconds < 0)
            throw new IllegalArgumentException("deltaSeconds must be finite and nonnegative");
        if (activeFrames != null) elapsed = Math.min(duration, elapsed + deltaSeconds);
    }

    /**
     * Draw last in the already-begun HUD batch. x/y are bottom-left HUD coordinates.
     * Use width=1280,height=360 for the authored logical size. Both atlas tiers
     * use these same draw dimensions. Caller controls projection and viewport.
     */
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        requireAlive();
        if (!isPlaying()) return;
        if (!batch.isDrawing()) throw new IllegalStateException("Call draw between batch.begin/end");
        finiteValue(x, "draw x");
        finiteValue(y, "draw y");
        positive(width, "overlay width");
        positive(height, "overlay height");
        float sx = width / cropWidth, sy = height / cropHeight;
        float oldColor = batch.getPackedColor();
        try {
            float[] commands = activeFrames[getFrameIndex()];
            for (int offset = 0; offset < commands.length; offset += STRIDE) {
                float opacity = commands[offset + 6];
                if (opacity <= 0) continue;
                float w = commands[offset + 3] * sx, h = commands[offset + 4] * sy;
                batch.setColor(1, 1, 1, opacity);
                batch.draw(regions[(int) commands[offset]],
                        x + commands[offset + 1] * sx, y + commands[offset + 2] * sy,
                        w / 2, h / 2, w, h, 1, 1, commands[offset + 5]);
            }
        } finally {
            batch.setPackedColor(oldColor);
        }
    }

    /** Use only with a logical 1920x1080 HUD viewport. */
    public void draw(SpriteBatch batch) {
        draw(batch, defaultX, defaultY, cropWidth, cropHeight);
    }

    /**
     * Copies [localX, localYBottom, width, height, alpha] for optional runtime text.
     * Reuse a caller-owned float[5]. Draw custom words only with a clean_* variant.
     * Coordinates are component-local logical pixels, not screen positions.
     */
    public void getTitleTransform(float[] output) {
        requireAlive();
        if (output == null || output.length < 5)
            throw new IllegalArgumentException("Title output must hold 5 floats");
        System.arraycopy(titleTransforms[getFrameIndex()], 0, output, 0, 5);
        if (!isPlaying()) output[4] = 0;
    }

    public boolean isPlaying() { return activeFrames != null && elapsed < duration && !disposed; }
    public String getVariant() { return variant; }
    public double getElapsedSeconds() { return elapsed; }
    public double getDurationSeconds() { return duration; }
    public int getFrameIndex() {
        return Math.min(frameCount - 1, (int) Math.floor(elapsed * fps + FRAME_EPSILON));
    }

    /** End the overlay immediately without affecting gameplay. */
    public void clear() { requireAlive(); activeFrames = null; variant = null; elapsed = 0; }

    @Override public void dispose() {
        if (disposed) return;
        disposed = true;
        atlas.dispose();
        activeFrames = null;
    }

    private void requireAlive() {
        if (disposed) throw new IllegalStateException("Milestone player has been disposed");
    }

    private static JsonValue required(JsonValue value, String key) {
        JsonValue result = value.get(key);
        if (result == null) throw new IllegalArgumentException("Missing manifest field: " + key);
        return result;
    }

    private static float finiteValue(float value, String name) {
        if (Float.isNaN(value) || Float.isInfinite(value))
            throw new IllegalArgumentException("Invalid " + name);
        return value;
    }

    private static double positive(double value, String name) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0)
            throw new IllegalArgumentException("Invalid " + name);
        return value;
    }

    private static void alpha(float value) {
        if (value < 0 || value > 1) throw new IllegalArgumentException("Alpha outside [0,1]");
    }
}
