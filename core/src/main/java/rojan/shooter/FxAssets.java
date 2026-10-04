package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public final class FxAssets{
    private FxAssets(){}
    private static Array<Texture> loadedTextures;
    public static TextureRegion[] phaseBeam;
    public static TextureRegion[] emitterFlash;
    public static TextureRegion[] beamContact;
    public static TextureRegion[] targetReticle;
    public static TextureRegion[] droneBurst;

    public static TextureRegion[] bombardmentPortalOpen;
    public static TextureRegion[] bombardmentPortalActive;
    public static TextureRegion[] bombardmentPortalClose;
    public static TextureRegion[] poisonPortalOpen;
    public static TextureRegion[] poisonPortalActive;
    public static TextureRegion[] poisonPortalRelease;
    public static TextureRegion[] poisonPortalClose;
    public static TextureRegion[] empPortalOpen;
    public static TextureRegion[] empPortalActive;
    public static TextureRegion[] empPortalClose;

    public static TextureRegion[] poisonFieldStart;
    public static TextureRegion[] poisonFieldLoop;
    public static TextureRegion[] poisonFieldEnd;


    public static Texture playerHealthFrameTexture;
    public static Texture playerHealthFillTexture;
    public static TextureRegion playerHealthFrame;
    public static TextureRegion playerHealthFill;

    public static void loadOnce(){
        if(loadedTextures != null)
            return;
        loadedTextures = new Array<>();
        phaseBeam = loadFrames(AssetPaths.FX.PHASE_BEAM,AssetPaths.FX.PHASE_BEAM_FRAMES);
        emitterFlash = loadFrames(AssetPaths.FX.EMITTER_FLASH,AssetPaths.FX.EMITTER_FLASH_FRAMES);
        beamContact = loadFrames(AssetPaths.FX.BEAM_CONTACT,AssetPaths.FX.BEAM_CONTACT_FRAMES);
        targetReticle = loadFrames(AssetPaths.FX.TARGET_RETICLE,AssetPaths.FX.TARGET_RETICLE_FRAMES);
        droneBurst = loadFrames(AssetPaths.FX.DRONE_BURST,AssetPaths.FX.DRONE_BURST_FRAMES);
        bombardmentPortalOpen = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_OPEN,AssetPaths.FX.BOMBARDMENT_PORTAL_OPEN_FRAMES);
        bombardmentPortalActive = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_ACTIVE,AssetPaths.FX.BOMBARDMENT_PORTAL_ACTIVE_FRAMES);
        bombardmentPortalClose = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_CLOSE,AssetPaths.FX.BOMBARDMENT_PORTAL_CLOSE_FRAMES);

        poisonPortalOpen = loadFrames(AssetPaths.FX.POISON_PORTAL_OPEN,AssetPaths.FX.POISON_PORTAL_OPEN_FRAMES);
        poisonPortalActive = loadFrames(AssetPaths.FX.POISON_PORTAL_ACTIVE,AssetPaths.FX.POISON_PORTAL_ACTIVE_FRAMES);
        poisonPortalRelease = loadFrames(AssetPaths.FX.POISON_PORTAL_RELEASE,AssetPaths.FX.POISON_PORTAL_RELEASE_FRAMES);
        poisonPortalClose = loadFrames(AssetPaths.FX.POISON_PORTAL_CLOSE,AssetPaths.FX.POISON_PORTAL_CLOSE_FRAMES);

        empPortalOpen = loadFrames(AssetPaths.FX.EMP_PORTAL_OPEN,AssetPaths.FX.EMP_PORTAL_OPEN_FRAMES);
        empPortalActive = loadFrames(AssetPaths.FX.EMP_PORTAL_ACTIVE,AssetPaths.FX.EMP_PORTAL_ACTIVE_FRAMES);
        empPortalClose = loadFrames(AssetPaths.FX.EMP_PORTAL_CLOSE,AssetPaths.FX.EMP_PORTAL_CLOSE_FRAMES);

        poisonFieldStart = loadFrames(AssetPaths.FX.POISON_FIELD_START,AssetPaths.FX.POISON_FIELD_START_FRAMES);
        poisonFieldLoop =loadFrames(AssetPaths.FX.POISON_FIELD_LOOP,AssetPaths.FX.POISON_FIELD_LOOP_FRAMES);
        poisonFieldEnd =loadFrames(AssetPaths.FX.POISON_FIELD_END,AssetPaths.FX.POISON_FIELD_END_FRAMES);

        playerHealthFrameTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_FRAME);
        playerHealthFillTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_FILL);
        playerHealthFrame = new TextureRegion(playerHealthFrameTexture);
        playerHealthFill = new TextureRegion(playerHealthFillTexture);
    }

    private static TextureRegion[] loadFrames(String prefix,int frameCount){
        TextureRegion[] frames = new TextureRegion[frameCount];
        for(int i=0;i<frameCount;i++){
            String frameNumber = String.format("%02d",i);
            Texture texture = new Texture(prefix + frameNumber + ".png");
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
        return frames;
    }

    public static void dispose(){
        if(loadedTextures == null)
            return;
        for(Texture texture : loadedTextures)
            texture.dispose();
        loadedTextures = null;
        playerHealthFrameTexture.dispose();
        playerHealthFillTexture.dispose();
    }
}