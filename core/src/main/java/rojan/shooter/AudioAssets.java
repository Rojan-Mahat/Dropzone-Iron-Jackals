package rojan.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

import rojan.shooter.LootCrate.CrateType;
import rojan.shooter.Weapon.WeaponType;

public final class AudioAssets{
    private AudioAssets(){}

    private static ObjectMap<String,Sound> sounds;
    private static ObjectIntMap<WeaponType> lastWeaponFireVariant;

    private static Music tacticalMusic;
    private static Music assaultMusic;
    private static Music menuMusic;
    private static Music currentMusic;

    private static float sfxVolume = 1f;
    private static float musicVolume = 1f;

    private static int lastRocketImpactVariant;
    private static int lastGoldVariant;
    private static int lastBodyHitVariant;
    private static int lastArmorHitVariant;

    public static void loadOnce(){
        if(sounds != null)
            return;

        sounds = new ObjectMap<>();
        lastWeaponFireVariant = new ObjectIntMap<>();

        tacticalMusic = Gdx.audio.newMusic(Gdx.files.internal(AssetPaths.Audio.GAMEPLAY_TACTICAL));
        assaultMusic = Gdx.audio.newMusic(Gdx.files.internal(AssetPaths.Audio.GAMEPLAY_ASSAULT));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(AssetPaths.Audio.MENU_ARMORY));

        tacticalMusic.setLooping(true);
        assaultMusic.setLooping(true);
        menuMusic.setLooping(true);
    }

    private static Sound getSound(String path){
        Sound sound = sounds.get(path);

        if(sound == null){
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            sounds.put(path,sound);
        }

        return sound;
    }

    private static void play(String path,float gain){
        getSound(path).play(gain*sfxVolume);
    }

    private static String getWeaponPrefix(WeaponType type){
        if(type == WeaponType.PISTOL)
            return "pistol";

        if(type == WeaponType.AR)
            return "ar";

        if(type == WeaponType.SMG)
            return "smg";

        if(type == WeaponType.SHOTGUN)
            return "shotgun";

        if(type == WeaponType.SNIPER)
            return "sniper";

        return "rocket_launcher";
    }

    private static int randomVariant(int amount,int previous){
        int variant = MathUtils.random(1,amount);

        if(amount > 1){
            while(variant == previous)
                variant = MathUtils.random(1,amount);
        }

        return variant;
    }

    public static void playWeaponFire(WeaponType type){
        int previous = lastWeaponFireVariant.get(type,0);
        int variant = randomVariant(4,previous);

        lastWeaponFireVariant.put(type,variant);

        String id = getWeaponPrefix(type)+"_fire_0"+variant;
        play(AssetPaths.Audio.weapon(id),0.78f);
    }

    public static void playWeaponReloadStart(WeaponType type){
        play(AssetPaths.Audio.weapon(getWeaponPrefix(type)+"_reload_start"),0.53f);
    }

    public static void playWeaponReloadInsert(WeaponType type){
        play(AssetPaths.Audio.weapon(getWeaponPrefix(type)+"_reload_insert"),0.53f);
    }

    public static void playWeaponReloadFinish(WeaponType type){
        play(AssetPaths.Audio.weapon(getWeaponPrefix(type)+"_reload_finish"),0.53f);
    }

    public static void playWeaponDryFire(WeaponType type){
        play(AssetPaths.Audio.weapon(getWeaponPrefix(type)+"_dry_fire"),0.50f);
    }

    public static void playWeaponEquip(WeaponType type){
        play(AssetPaths.Audio.weapon(getWeaponPrefix(type)+"_equip"),0.53f);
    }

    public static void playReloadCancel(){
        play(AssetPaths.Audio.weapon("reload_cancel"),0.43f);
    }

    public static void playRocketImpact(){
        lastRocketImpactVariant = randomVariant(3,lastRocketImpactVariant);
        play(AssetPaths.Audio.weapon("rocket_impact_0"+lastRocketImpactVariant),0.83f);
    }

    public static void playScopeWiden(){
        play(AssetPaths.Audio.weapon("view_range_widen"),0.45f);
    }

    public static void playScopeNarrow(){
        play(AssetPaths.Audio.weapon("view_range_narrow"),0.45f);
    }

    public static void playHeadshot(){
        play(AssetPaths.Audio.combat("headshot_confirm"),0.65f);
    }

    public static void playBodyHit(){
        lastBodyHitVariant = randomVariant(3,lastBodyHitVariant);
        play(AssetPaths.Audio.combat("body_hit_0"+lastBodyHitVariant),0.65f);
    }

    public static void playArmorHit(){
        lastArmorHitVariant = randomVariant(3,lastArmorHitVariant);
        play(AssetPaths.Audio.combat("armor_hit_0"+lastArmorHitVariant),0.65f);
    }

    public static void playKill(){
        play(AssetPaths.Audio.combat("kill_confirm"),0.65f);
    }

    public static void playGoldGain(){
        lastGoldVariant = randomVariant(3,lastGoldVariant);
        play(AssetPaths.Audio.ui("ui_gold_gain_0"+lastGoldVariant),0.60f);
    }

    public static void playCrateSpawn(CrateType type){
        if(type == CrateType.COMMON)
            play(AssetPaths.Audio.world("crate_spawn_common"),0.65f);

        else if(type == CrateType.RARE)
            play(AssetPaths.Audio.world("crate_spawn_rare"),0.65f);

        else if(type == CrateType.SUPER_RARE)
            play(AssetPaths.Audio.world("crate_spawn_super_rare"),0.65f);

        else
            play(AssetPaths.Audio.world("crate_spawn_special"),0.65f);
    }

    public static void playCrateOpen(CrateType type){
        if(type == CrateType.COMMON)
            play(AssetPaths.Audio.world("crate_open_common"),0.65f);

        else if(type == CrateType.RARE)
            play(AssetPaths.Audio.world("crate_open_rare"),0.65f);

        else if(type == CrateType.SUPER_RARE)
            play(AssetPaths.Audio.world("crate_open_super_rare"),0.65f);

        else
            play(AssetPaths.Audio.world("crate_open_special"),0.65f);
    }

    public static void playPowerActivate(String id){
        if("health".equals(id)){
            play(AssetPaths.Audio.power("health_activate"),0.65f);
            return;
        }

        if("jetpack".equals(id)){
            play(AssetPaths.Audio.power("jetpack_refill_activate"),0.65f);
            return;
        }

        play(AssetPaths.Audio.power(id+"_activate"),0.65f);
    }

    public static void playPowerExpire(String id){
        play(AssetPaths.Audio.power(id+"_expire"),0.65f);
    }

    public static void playShieldHit(){
        int variant = MathUtils.random(1,3);
        play(AssetPaths.Audio.power("shield_hit_0"+variant),0.65f);
    }

    public static void playShieldBreak(){
        play(AssetPaths.Audio.power("shield_break"),0.65f);
    }

    public static void playJackpot(){
        play(AssetPaths.Audio.power("jackpot_reward"),0.65f);
    }

    public static void playBiggerJackpot(){
        play(AssetPaths.Audio.power("bigger_jackpot_reward"),0.65f);
    }

    public static void playUiPanelOpen(){
        play(AssetPaths.Audio.ui("ui_panel_open"),0.60f);
    }

    public static void playUiPanelClose(){
        play(AssetPaths.Audio.ui("ui_panel_close"),0.60f);
    }

    public static void playUiConfirm(){
        play(AssetPaths.Audio.ui("ui_confirm"),0.60f);
    }

    public static void playUiCancel(){
        play(AssetPaths.Audio.ui("ui_cancel"),0.60f);
    }

    public static void playRunStart(){
        play(AssetPaths.Audio.world("run_start"),0.65f);
    }

    public static void playRunTerminated(){
        play(AssetPaths.Audio.world("run_terminated"),0.65f);
    }

    public static void playTacticalMusic(){
        playMusic(tacticalMusic,0.35f);
    }

    public static void playAssaultMusic(){
        playMusic(assaultMusic,0.35f);
    }

    public static void playMenuMusic(){
        playMusic(menuMusic,0.35f);
    }

    private static void playMusic(Music music,float gain){
        if(currentMusic == music && music.isPlaying())
            return;

        if(currentMusic != null)
            currentMusic.stop();

        currentMusic = music;
        currentMusic.setVolume(gain*musicVolume);
        currentMusic.play();
    }

    public static void pauseMusic(){
        if(currentMusic != null)
            currentMusic.pause();
    }

    public static void resumeMusic(){
        if(currentMusic != null)
            currentMusic.play();
    }

    public static void setSfxVolume(float volume){
        sfxVolume = MathUtils.clamp(volume,0f,1f);
    }

    public static void setMusicVolume(float volume){
        musicVolume = MathUtils.clamp(volume,0f,1f);

        if(currentMusic != null)
            currentMusic.setVolume(0.35f*musicVolume);
    }
    public static void playDifficultyEscalation(){
        play(AssetPaths.Audio.world("difficulty_escalation"),0.75f);
    }
    public static void playKeyPickup(){
        play(AssetPaths.Audio.world("key_pickup"),0.70f);
    }
    public static void dispose(){
        if(sounds != null){
            for(Sound sound : sounds.values())
                sound.dispose();

            sounds.clear();
            sounds = null;
        }
        if(tacticalMusic != null)
            tacticalMusic.dispose();

        if(assaultMusic != null)
            assaultMusic.dispose();

        if(menuMusic != null)
            menuMusic.dispose();
    }
}