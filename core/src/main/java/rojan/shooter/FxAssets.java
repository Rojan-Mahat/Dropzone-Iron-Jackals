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
    public static Texture playerHealthIconTexture;
    public static TextureRegion playerHealthFrame;
    public static TextureRegion playerHealthFill;
    public static TextureRegion playerHealthIcon;


    public static Texture playerJetpackFrameTexture;
    public static Texture playerJetpackFillTexture;
    public static Texture playerJetpackIconTexture;
    public static TextureRegion playerJetpackFrame;
    public static TextureRegion playerJetpackFill;
    public static TextureRegion playerJetpackIcon;

    public static TextureRegion[] playerPoisonStart;
    public static TextureRegion[] playerPoisonLingering;
    public static TextureRegion[] playerPoisonEnd;

    public static TextureRegion[] playerStunStart;
    public static TextureRegion[] playerStunLingering;
    public static TextureRegion[] playerStunEnd;

    public static TextureRegion[] empPulse;

    public static TextureRegion[] bombardmentRainBolt;
    public static TextureRegion[] bombardmentRainImpact;

    public static Texture reloadIconTexture;
    public static TextureRegion reloadIcon;

    public static Texture emptyMagazineTexture;
    public static TextureRegion emptyMagazineIcon;

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
        playerHealthIconTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_ICON);

        playerJetpackFrameTexture = new Texture(AssetPaths.Hud.JETPACK_FRAME);
        playerJetpackFillTexture = new Texture(AssetPaths.Hud.JETPACK_FILL);
        playerJetpackFrame = new TextureRegion(playerJetpackFrameTexture);
        playerJetpackFill = new TextureRegion(playerJetpackFillTexture);
        playerJetpackIconTexture = new Texture(AssetPaths.Hud.PLAYER_JETPACK_ICON);


        playerPoisonStart = loadFrames(AssetPaths.FX.PLAYER_POISON_START, AssetPaths.FX.PLAYER_POISON_START_FRAMES);
        playerPoisonLingering = loadFrames(AssetPaths.FX.PLAYER_POISON_LINGERING, AssetPaths.FX.PLAYER_POISON_LINGERING_FRAMES);
        playerPoisonEnd = loadFrames(AssetPaths.FX.PLAYER_POISON_END, AssetPaths.FX.PLAYER_POISON_END_FRAMES);
        playerHealthIcon = new TextureRegion(playerHealthIconTexture);
        playerJetpackIcon = new TextureRegion(playerJetpackIconTexture);

        playerStunStart = loadFrames(AssetPaths.FX.PLAYER_STUN_START, AssetPaths.FX.PLAYER_STUN_START_FRAMES);
        playerStunLingering = loadFrames(AssetPaths.FX.PLAYER_STUN_LINGERING,AssetPaths.FX.PLAYER_STUN_LINGERING_FRAMES);
        playerStunEnd = loadFrames(AssetPaths.FX.PLAYER_STUN_END,AssetPaths.FX.PLAYER_STUN_END_FRAMES);

        empPulse = loadFrames(AssetPaths.FX.EMP_PULSE,AssetPaths.FX.EMP_PULSE_FRAMES);

        bombardmentRainBolt = loadFrames(AssetPaths.FX.BOMBARDMENT_RAIN_BOLT,AssetPaths.FX.BOMBARDMENT_RAIN_BOLT_FRAMES);
        bombardmentRainImpact = loadFrames(AssetPaths.FX.BOMBARDMENT_RAIN_IMPACT,AssetPaths.FX.BOMBARDMENT_RAIN_IMPACT_FRAMES);

        emptyMagazineTexture = new Texture(AssetPaths.Hud.EMPTY_MAGAZINE);
        emptyMagazineIcon = new TextureRegion(emptyMagazineTexture);

        reloadIconTexture = new Texture(AssetPaths.Hud.RELOAD_ICON);
        reloadIcon = new TextureRegion(reloadIconTexture);
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
        playerHealthIconTexture.dispose();
        reloadIconTexture.dispose();
        emptyMagazineTexture.dispose();
    }
}