package rojan.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Optional one-shot artwork player. Screen changes and input remain caller-owned.
 * Construct, update, draw and dispose on the render thread. The caller's batch
 * must be ended before draw(). This component owns its own SpriteBatch and atlas.
 */
public final class DropzoneStartupIntro implements Disposable {
    private static final int STRIDE = 7;
    // Only corrects floating-point boundary roundoff; not a visible timing offset.
    private static final double FRAME_EPSILON = 0.00001;

    private final TextureAtlas atlas;
    private final SpriteBatch batch;
    private final TextureRegion[] regions;
    private final float[][] frames;
    private final double fps, duration, menuRevealEvent;
    private final int menuRevealFrame;
    private final float canvasWidth, canvasHeight;
    private double elapsed;
    private boolean disposed;

    /**
     * Example directory: "ui/dropzone_iron_jackals_intro".
     * use2x selects the high-resolution atlas; draw coordinates are identical.
     */
    public DropzoneStartupIntro(String internalDirectory, boolean use2x) {
        FileHandle directory = Gdx.files.internal(internalDirectory);
        JsonValue manifest = new JsonReader().parse(directory.child("playback.json"));
        JsonValue canvas = required(manifest, "canvas");
        canvasWidth = positive(canvas.getFloat(0), "canvas width");
        canvasHeight = positive(canvas.getFloat(1), "canvas height");
        fps = positive(manifest.getDouble("fps"), "fps");
        duration = positive(manifest.getDouble("duration"), "duration");
        menuRevealEvent = manifest.getDouble("menu_reveal_event");
        if (!finite(menuRevealEvent) || menuRevealEvent < 0 || menuRevealEvent >= duration)
            throw new IllegalArgumentException("Invalid menu_reveal_event");

        JsonValue regionNames = required(manifest, "regions");
        JsonValue frameList = required(manifest, "frames");
        if (regionNames.size == 0 || frameList.size == 0)
            throw new IllegalArgumentException("Empty regions or frames");
        if (frameList.size != manifest.getInt("frame_count"))
            throw new IllegalArgumentException("frame_count does not match frames");
        if (Math.abs(frameList.size - duration * fps) > 0.0001)
            throw new IllegalArgumentException("Frame count, fps and duration disagree");
        menuRevealFrame = (int) Math.ceil(menuRevealEvent * fps - FRAME_EPSILON);
        if (menuRevealFrame >= frameList.size)
            throw new IllegalArgumentException("Menu reveal lies beyond the clip");

        frames = new float[frameList.size][];
        int frameIndex = 0;
        for (JsonValue frame = frameList.child; frame != null; frame = frame.next) {
            float[] commands = new float[frame.size * STRIDE];
            int offset = 0;
            for (JsonValue command = frame.child; command != null; command = command.next) {
                if (command.size != STRIDE)
                    throw new IllegalArgumentException("Draw command must contain 7 values");
                for (int n = 0; n < STRIDE; n++) {
                    float value = command.getFloat(n);
                    if (Float.isNaN(value) || Float.isInfinite(value))
                        throw new IllegalArgumentException("Non-finite draw command");
                    commands[offset + n] = value;
                }
                float id = commands[offset];
                if (id < 0 || id >= regionNames.size || id != (int) id)
                    throw new IllegalArgumentException("Invalid region index");
                if (commands[offset + 3] <= 0 || commands[offset + 4] <= 0)
                    throw new IllegalArgumentException("Draw dimensions must be positive");
                float alpha = commands[offset + 6];
                if (alpha < 0 || alpha > 1)
                    throw new IllegalArgumentException("Draw alpha must be in [0, 1]");
                offset += STRIDE;
            }
            frames[frameIndex++] = commands;
        }

        TextureAtlas loadedAtlas = null;
        SpriteBatch createdBatch = null;
        try {
            loadedAtlas = new TextureAtlas(directory.child(
                    manifest.getString(use2x ? "atlas_2x" : "atlas_1x")));
            regions = new TextureRegion[regionNames.size];
            for (int i = 0; i < regions.length; i++) {
                String name = regionNames.getString(i);
                TextureAtlas.AtlasRegion region = loadedAtlas.findRegion(name);
                if (region == null)
                    throw new IllegalArgumentException("Missing atlas region: " + name);
                if (region.rotate || region.offsetX != 0 || region.offsetY != 0
                        || region.originalWidth != region.packedWidth
                        || region.originalHeight != region.packedHeight)
                    throw new IllegalArgumentException("Expected untrimmed, unrotated region: " + name);
                regions[i] = region;
            }
            createdBatch = new SpriteBatch();
            createdBatch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
                    GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } catch (RuntimeException error) {
            if (createdBatch != null) createdBatch.dispose();
            if (loadedAtlas != null) loadedAtlas.dispose();
            throw error;
        }
        atlas = loadedAtlas;
        batch = createdBatch;
    }

    /** Advance without looping. Large deltas still preserve the reveal condition. */
    public void update(float deltaSeconds) {
        requireAlive();
        if (Float.isNaN(deltaSeconds) || Float.isInfinite(deltaSeconds) || deltaSeconds < 0)
            throw new IllegalArgumentException("deltaSeconds must be finite and nonnegative");
        elapsed = Math.min(duration, elapsed + deltaSeconds);
    }

    /**
     * Draw last over the active menu/screen, after ending any existing batch.
     * x/y are the bottom-left letterbox origin in the supplied projection units.
     * If the camera already uses a logical 1920x1080 viewport, use 0, 0, 1.
     */
    public void draw(Matrix4 projection, float x, float y, float scale) {
        requireAlive();
        if (isFinished()) return;
        positive(scale, "scale");
        if (!finite(x) || !finite(y)) throw new IllegalArgumentException("Invalid draw origin");
        batch.setProjectionMatrix(projection);
        batch.begin();
        try {
            float[] commands = frames[getFrameIndex()];
            for (int offset = 0; offset < commands.length; offset += STRIDE) {
                float alpha = commands[offset + 6];
                if (alpha <= 0) continue;
                float width = commands[offset + 3] * scale;
                float height = commands[offset + 4] * scale;
                batch.setColor(1, 1, 1, alpha);
                batch.draw(regions[(int) commands[offset]],
                        x + commands[offset + 1] * scale,
                        y + commands[offset + 2] * scale,
                        width / 2, height / 2, width, height, 1, 1,
                        commands[offset + 5]);
            }
        } finally {
            batch.end();
        }
    }

    /** True from the fully covered frame onward. The caller latches its screen change once. */
    public boolean shouldRevealMenu() {
        return isFinished() || getFrameIndex() >= menuRevealFrame;
    }

    public boolean isFinished() { return elapsed >= duration; }
    public double getElapsedSeconds() { return elapsed; }
    public double getDurationSeconds() { return duration; }
    public double getMenuRevealEventSeconds() { return menuRevealEvent; }
    public float getCanvasWidth() { return canvasWidth; }
    public float getCanvasHeight() { return canvasHeight; }

    public int getFrameIndex() {
        return Math.min(frames.length - 1, (int) Math.floor(elapsed * fps + FRAME_EPSILON));
    }

    /** Intentional replay only. A normal screen change should not reset a startup intro. */
    public void reset() { requireAlive(); elapsed = 0; }

    /** Optional skip: reveals the menu immediately; no callback or screen mutation. */
    public void finish() { requireAlive(); elapsed = duration; }

    @Override public void dispose() {
        if (disposed) return;
        disposed = true;
        batch.dispose();
        atlas.dispose();
    }

    private void requireAlive() {
        if (disposed) throw new IllegalStateException("Intro has been disposed");
    }

    private static JsonValue required(JsonValue parent, String key) {
        JsonValue value = parent.get(key);
        if (value == null) throw new IllegalArgumentException("Missing manifest field: " + key);
        return value;
    }

    private static boolean finite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private static double positive(double value, String name) {
        if (!finite(value) || value <= 0) throw new IllegalArgumentException("Invalid " + name);
        return value;
    }

    private static float positive(float value, String name) {
        positive((double) value, name);
        return value;
    }
}
