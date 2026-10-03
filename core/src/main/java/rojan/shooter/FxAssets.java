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

    public static void loadOnce(){
        if(loadedTextures != null)
            return;
        loadedTextures = new Array<>();
        phaseBeam = loadFrames(AssetPaths.FX.PHASE_BEAM,AssetPaths.FX.PHASE_BEAM_FRAMES);
        emitterFlash = loadFrames(AssetPaths.FX.EMITTER_FLASH,AssetPaths.FX.EMITTER_FLASH_FRAMES);
        beamContact = loadFrames(AssetPaths.FX.BEAM_CONTACT,AssetPaths.FX.BEAM_CONTACT_FRAMES);
        targetReticle = loadFrames(AssetPaths.FX.TARGET_RETICLE,AssetPaths.FX.TARGET_RETICLE_FRAMES);
        droneBurst = loadFrames(AssetPaths.FX.DRONE_BURST,AssetPaths.FX.DRONE_BURST_FRAMES);
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
    }
}