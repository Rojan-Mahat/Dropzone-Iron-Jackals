package rojan.shooter;
public final class AssetPaths {
    private AssetPaths() {}

    public static class Hud{
        public static final String PLAYER_HEALTH_FRAME = "ui/hud/player_health_frame.png";
        public static final String PLAYER_HEALTH_FILL = "ui/hud/player_health_fill.png";
        public static final String PLAYER_HEALTH_ICON = "ui/hud/hp_icon.png";

        public static final String JETPACK_FRAME = "ui/hud/jetpack_fuel_frame.png";
        public static final String JETPACK_FILL = "ui/hud/jetpack_fuel_fill.png";
        public static final String PLAYER_JETPACK_ICON = "ui/hud/jetpack_icon.png";

        public static final String AMMO_PANEL = "ui/hud/ammo_panel.png";
        public static final String AMMO_ICON = "ui/hud/ammo_icon.png";
        public static final String NO_AMMO_ICON = "ui/hud/no_ammo_icon.png";
        public static final String RELOAD_ICON = "ui/hud/reload_icon.png";

        public static final String SCOPE_OVERLAY = "ui/hud/scope_overlay.png";

        public static final String SCORE_DASHBOARD = "ui/hud/score_dashboard_frame.png";
        public static final String GOLD_ICON = "ui/hud/gold_icon.png";
        public static final String SCORE_ICON = "ui/hud/score_icon.png";
        public static final String BEST_SCORE_ICON = "ui/hud/best_score_icon.png";

        public static final String INVENTORY_BAG ="ui/inventory/inventory_bag_button.png";
        public static final String INVENTORY_BAG_HOVER ="ui/inventory/inventory_bag_button_hover.png";
        public static final String INVENTORY_BAG_PRESSED = "ui/inventory/inventory_bag_button_pressed.png";
        public static final String INVENTORY_PANEL = "ui/inventory/inventory_panel_icon.png";
        public static final String INVENTORY_PANEL_GUN = "ui/inventory/inventory_panel_gun.png";

        public static final String INVENTORY_WEAPON_LOCKED ="ui/inventory/weapon_locked.png";
        public static final String INVENTORY_WEAPON_EQUIPPED ="ui/inventory/weapon_equipped.png";

        public static final String POWER_FRAME = "ui/hud/power/primary_frame.png";
        public static final String POWER_NORMAL = "ui/hud/power/primary_tier_normal.png";
        public static final String POWER_SPICY = "ui/hud/power/primary_tier_spicy.png";
        public static final String POWER_MORE_SPICY = "ui/hud/power/primary_tier_more_spicy.png";
        public static final String POWER_SPECIAL = "ui/hud/power/primary_tier_special.png";

        public static final String SCOPE_BUTTON = "ui/hud/scope/scope_button_normal.png";
        public static final String SCOPE_BUTTON_HOVER = "ui/hud/scope/scope_button_hover.png";
        public static final String SCOPE_BUTTON_PRESSED = "ui/hud/scope/scope_button_pressed.png";
        public static final String KILL_BADGE = "ui/hud/kill/kill_badge.png";

        public static final String KEY_ICON = "ui/hud/keyIcon.png";
}
    

    public static final class EnemyProjectile {
        private EnemyProjectile() {}
        public static final String BASE = "sprites/enemy_projectiles/";
        public static final String HEAVY_PROJECTILE= BASE + "heavy.png";
        public static final String RIFLEMAN_PROJECTILE= BASE + "rifleman.png";
    }

    public static final class Shop{
        private Shop(){}

        public static final String BASE = "ui/shop/";
        public static final String ICON = BASE+"shopIcon.png";
        public static final String PANEL = BASE+"shop_panel.png";
        public static final String ACTION_BUY = BASE+"action_buy.png";
        public static final String ACTION_BUY_HOVER = BASE+"action_buy_hover.png";
        public static final String ACTION_BUY_PRESSED = BASE+"action_buy_pressed.png";
        public static final String ACTION_OWNED = BASE+"action_owned.png";
        public static final String ACTION_EQUIPPED = BASE+"action_equipped.png";
        public static final String ACTION_NOT_ENOUGH_GOLD = BASE+"action_not_enough_gold.png";
        public static final String CARD_HOVER = BASE+"card_hover.png";
        public static final String CARD_SELECTED = BASE+"card_selected.png";
        public static final String CATEGORY_HOVER = BASE+"category_hover.png";
        public static final String CATEGORY_SELECTED = BASE+"category_selected.png";
        public static final String CLOSE_NORMAL = BASE+"close_normal.png";
        public static final String CLOSE_HOVER = BASE+"close_hover.png";
        public static final String CLOSE_PRESSED = BASE+"close_pressed.png";
        public static final String WEAPONS_CATALOG = BASE+"weapons_catalog.png";
        public static final String AMMO_CATALOG = BASE+"ammo_catalog.png";
        public static final String SPECIAL_CATALOG = BASE+"special_catalog.png";
        public static final String WEAPON_HOVER = BASE+"weapon_hover.png";
        public static final String WEAPON_SELECTED = BASE+"weapon_selected.png";
        public static final String WEAPON_LOCKED = BASE+"weapon_locked.png";
        public static final String WEAPON_UNAVAILABLE = BASE+"weapon_unavailable.png";
        public static final String WEAPON_OWNED = BASE+"weapon_owned.png";
        public static final String WEAPON_EQUIPPED = BASE+"weapon_equipped.png";
        public static final String AMMO_HOVER = BASE+"ammo_hover.png";
        public static final String AMMO_SELECTED = BASE+"ammo_selected.png";
        public static final String AMMO_UNAVAILABLE = BASE+"ammo_unavailable.png";
        public static final String SPECIAL_HOVER = BASE+"special_hover.png";
        public static final String SPECIAL_SELECTED = BASE+"special_selected.png";
        public static final String SPECIAL_LOCKED = BASE+"special_locked.png";
        public static final String SPECIAL_UNAVAILABLE = BASE+"special_unavailable.png";
        public static final String AMMO_PISTOL = BASE+"ammo_pistol.png";
        public static final String AMMO_AR = BASE+"ammo_ar.png";
        public static final String AMMO_SMG = BASE+"ammo_smg.png";
        public static final String AMMO_SHOTGUN = BASE+"ammo_shotgun.png";
        public static final String AMMO_SNIPER = BASE+"ammo_sniper.png";
        public static final String AMMO_ROCKET = BASE+"ammo_rocket.png";
        public static final String SPECIAL_FIELD_KEY = BASE+"special_field_key.png";
        public static final String SPECIAL_FULL_RESTOCK = BASE+"special_full_restock.png";
        public static final String SPECIAL_COMBAT_CACHE = BASE+"special_combat_cache.png";
        public static final String SPECIAL_ELITE_CACHE = BASE+"special_elite_cache.png";
        public static final String SPECIAL_CLASSIFIED = BASE+"special_classified.png";
        public static final String SPECIAL_LOCKED_FUTURE = BASE+"special_locked_future.png";
    }

    public static final class FX{
        private FX(){}
        public static final String BASE = "sprites/fx/";
        public static final String PHASE_BEAM = BASE + "phase_beam_";
        public static final int PHASE_BEAM_FRAMES = 8;
        public static final String EMITTER_FLASH = BASE + "emitter_flash_";
        public static final int EMITTER_FLASH_FRAMES = 8;
        public static final String BEAM_CONTACT = BASE + "beam_contact_";
        public static final int BEAM_CONTACT_FRAMES = 8;
        public static final String TARGET_RETICLE = BASE + "target_reticle_";
        public static final int TARGET_RETICLE_FRAMES = 8;
        public static final String DRONE_BURST = BASE + "drone_burst_";
        public static final int DRONE_BURST_FRAMES = 10;
        public static final String HEAVY_PORTAL_BASE = BASE + "heavy_portal/";
        public static final String BOMBARDMENT_PORTAL_BASE = HEAVY_PORTAL_BASE + "bombardment/";
        public static final String BOMBARDMENT_PORTAL_OPEN = BOMBARDMENT_PORTAL_BASE + "open_";
        public static final int BOMBARDMENT_PORTAL_OPEN_FRAMES = 7;
        public static final String BOMBARDMENT_PORTAL_ACTIVE = BOMBARDMENT_PORTAL_BASE + "active_";
        public static final int BOMBARDMENT_PORTAL_ACTIVE_FRAMES = 12;
        public static final String BOMBARDMENT_PORTAL_CLOSE = BOMBARDMENT_PORTAL_BASE + "close_";
        public static final int BOMBARDMENT_PORTAL_CLOSE_FRAMES = 8;
        public static final String POISON_PORTAL_BASE = HEAVY_PORTAL_BASE + "poison/";
        public static final String POISON_PORTAL_OPEN = POISON_PORTAL_BASE + "open_";
        public static final int POISON_PORTAL_OPEN_FRAMES = 8;
        public static final String POISON_PORTAL_ACTIVE = POISON_PORTAL_BASE + "active_";
        public static final int POISON_PORTAL_ACTIVE_FRAMES = 12;
        public static final String POISON_PORTAL_RELEASE = POISON_PORTAL_BASE + "release_";
        public static final int POISON_PORTAL_RELEASE_FRAMES = 8;
        public static final String POISON_PORTAL_CLOSE = POISON_PORTAL_BASE + "close_";
        public static final int POISON_PORTAL_CLOSE_FRAMES = 8;
        public static final String EMP_PORTAL_BASE = HEAVY_PORTAL_BASE + "emp/";
        public static final String EMP_PORTAL_OPEN = EMP_PORTAL_BASE + "open_";
        public static final int EMP_PORTAL_OPEN_FRAMES = 8;
        public static final String EMP_PORTAL_ACTIVE = EMP_PORTAL_BASE + "active_";
        public static final int EMP_PORTAL_ACTIVE_FRAMES = 12;
        public static final String EMP_PORTAL_CLOSE = EMP_PORTAL_BASE + "close_";
        public static final int EMP_PORTAL_CLOSE_FRAMES = 8;
        public static final String POISON_FIELD_BASE =HEAVY_PORTAL_BASE + "poison_field/";
        public static final String POISON_FIELD_START =POISON_FIELD_BASE + "poison_field_start/";
        public static final int POISON_FIELD_START_FRAMES = 8;
        public static final String POISON_FIELD_LOOP =POISON_FIELD_BASE + "poison_field_loop/";
        public static final int POISON_FIELD_LOOP_FRAMES = 12;
        public static final String POISON_FIELD_END =POISON_FIELD_BASE + "poison_field_end/";
        public static final int POISON_FIELD_END_FRAMES = 8;
        public static final String PLAYER_STATUS_BASE = BASE + "player_status/";
        public static final String PLAYER_POISON_BASE = PLAYER_STATUS_BASE + "poison/";
        public static final String PLAYER_POISON_START = PLAYER_POISON_BASE + "start/";
        public static final int PLAYER_POISON_START_FRAMES = 6;
        public static final String PLAYER_POISON_LINGERING = PLAYER_POISON_BASE + "lingering/";
        public static final int PLAYER_POISON_LINGERING_FRAMES = 12;
        public static final String PLAYER_POISON_END = PLAYER_POISON_BASE + "end/";
        public static final int PLAYER_POISON_END_FRAMES = 6;
        public static final String PLAYER_STUN_BASE = PLAYER_STATUS_BASE + "stun/";
        public static final String PLAYER_STUN_START = PLAYER_STUN_BASE + "start/";
        public static final int PLAYER_STUN_START_FRAMES = 6;
        public static final String PLAYER_STUN_LINGERING = PLAYER_STUN_BASE + "lingering/";
        public static final int PLAYER_STUN_LINGERING_FRAMES = 12;
        public static final String PLAYER_STUN_END = PLAYER_STUN_BASE + "end/";
        public static final int PLAYER_STUN_END_FRAMES = 6;
        public static final String EMP_PULSE = HEAVY_PORTAL_BASE + "emp_pulse/";
        public static final int EMP_PULSE_FRAMES = 12;
        public static final String BOMBARDMENT_RAIN_BOLT = BASE + "heavy_portal/bombardment_assets/projectile/";
        public static final int BOMBARDMENT_RAIN_BOLT_FRAMES = 6;
        public static final String BOMBARDMENT_RAIN_IMPACT = BASE + "heavy_portal/bombardment_assets/impact/";
        public static final int BOMBARDMENT_RAIN_IMPACT_FRAMES = 8;
        public static final String POWER_AURA_BASE = BASE+"power_auras/";
        public static String powerAuraBack(String id){
            return POWER_AURA_BASE+id+"/back.png";
        }
        public static String powerAuraFront(String id){
            return POWER_AURA_BASE+id+"/front.png";
        }
        public static final String RELOAD_LOCK_BASE = BASE+"reload_lock/";
        public static final String RELOAD_RECEIVER = RELOAD_LOCK_BASE+"receiver_2x.png";
        public static final String RELOAD_MUZZLE = RELOAD_LOCK_BASE+"muzzle_2x.png";
    }

    public static final class Map {
        private Map() {}
        public static final String EARTH = "map/earth/map.tmx";
        public static final String TERRAIN_TILESET = "map/earth/terrain.tsx";
        public static final String BACKGROUND = "map/background/background.png";
        public static final String MAP_PIECES = "map/map_pieces/";
    }

    public static final class Player {
        private Player() {}
        public static final class Soldier1 {
            private Soldier1() {}
            public static final String BASE = "sprites/soldiers/player1/";
            public static final String IDLE = BASE + "idle/idle_";
            public static final int IDLE_FRAMES = 12;

            // These exported files are currently named ok_000.png ... ok_011.png.
            public static final String IDLE_AIM = BASE + "idleAim/ok_";
            public static final int IDLE_AIM_FRAMES = 12;
            
            public static final String WALK = BASE + "walk/walk_";
            public static final int WALK_FRAMES = 12;
            
            public static final String WALK_SHOOT = BASE + "walkShoot/walkShoot_";
            public static final int WALK_SHOOT_FRAMES = 12;

            public static final String RUN = BASE + "run/run_";
            public static final int RUN_FRAMES = 12;

            public static final String RUN_SHOOT = BASE + "runShoot/runShoot_";
            public static final int RUN_SHOOT_FRAMES = 12;

            public static final String CROUCH = BASE + "crouch/crouch_";
            public static final int CROUCH_FRAMES = 12;

            public static final String CROUCH_AIM = BASE + "crouchAim/crouchAim_";
            public static final int CROUCH_AIM_FRAMES = 12;

            public static final String CROUCH_SHOOT = BASE + "crouchShoot/crouchShoot_";
            public static final int CROUCH_SHOOT_FRAMES = 3;

            public static final String JUMP = BASE + "jump/jump_";
            public static final int JUMP_FRAMES = 12;

            public static final String JUMP_SHOOT = BASE + "jumpShoot/jumpShoot_";
            public static final int JUMP_SHOOT_FRAMES = 12;

            public static final String SHOOT = BASE + "shoot/shoot_";
            public static final int SHOOT_FRAMES = 3;

            public static final String HURT = BASE + "hurt/hurt_";
            public static final int HURT_FRAMES = 12;

            public static final String DEAD = BASE + "dead/dead_";
            public static final int DEAD_FRAMES = 12;

            public static final String CLIMB = BASE + "climb/climb_";
            public static final int CLIMB_FRAMES = 12;

            public static final String MELEE = BASE + "melee/melee_";
            public static final int MELEE_FRAMES = 7;

            public static final String CROUCH_MELEE = BASE + "crouchMelee/crouchMelee_";
            public static final int CROUCH_MELEE_FRAMES = 7;

            public static final String JUMP_MELEE = BASE + "jumpMelee/jumpMelee_";
            public static final int JUMP_MELEE_FRAMES = 7;

            public static final String THROW = BASE + "throw/throw_";
            public static final int THROW_FRAMES = 9;

            public static final String CROUCH_THROW = BASE + "crouchThrow/crouchThrow_";
            public static final int CROUCH_THROW_FRAMES = 9;

            public static final String JUMP_THROW = BASE + "jumpThrow/jumpThrow_";
            public static final int JUMP_THROW_FRAMES = 9;

            public static final String HAPPY = BASE + "happy/happy_";
            public static final int HAPPY_FRAMES = 12;
        }
    }
    public static final class JetPack {
        public static final String jetpack = "sprites/jetpack/jetPack.png";
        public static final String jetpackFlames = "sprites/jetpack/jetpackFlames.png";
        public static final String jetpackDisabled = "sprites/jetpack/jetpack_disabled.png";
    }

    public static final class Data {
        public static final String SOLDIER1_GRIPS = "data/player1/soldier1_grips.json";
    }

    public static final class Weapons {
        private Weapons() {}
            public static final String BASE = "sprites/guns/gameplay/";
            public static final String PISTOL = BASE+"pistol.png";
            public static final String AR = BASE+"ar.png";
            public static final String SMG = BASE+"smg.png";
            public static final String SHOTGUN = BASE+"shotgun.png";
            public static final String SNIPER =  BASE+"sniper.png";
            public static final String ROCKET_LAUNCHER =BASE+"rocket_launcher.png";
    }

    public static final class Bullets {
        private Bullets() {}
        public static final String BASE = "sprites/bullet/";
        public static final String PISTOL = BASE+"pistol.png";
        public static final String AR = BASE+"ar.png";
        public static final String SMG = BASE+"smg.png";
        public static final String SHOTGUN = BASE+"shotgun.png";
        public static final String SNIPER = BASE+"sniper.png";
        public static final String ROCKET_LAUNCHER = BASE+"rocket_launcher.png";
    }

    public static final class Muzzle {
        private Muzzle() {}

        public static final String BASE = "sprites/muzzle/";

        public static final String ORANGE = BASE + "OrangeMuzzle__";
        public static final int ORANGE_FRAMES = 10;

        public static final String YELLOW = BASE + "YellowMuzzle__";
        public static final int YELLOW_FRAMES = 10;
    }

    public static final class Enemies {
        private Enemies() {}
        public static final class IronJackals {
            private IronJackals() {}
            public static final class Rifleman {
                private Rifleman() {}
                public static final String BASE = "sprites/enemies/iron_jackals/jackal_rifleman/";

                public static final String IDLE = BASE + "idle_";
                public static final int IDLE_FRAMES = 6;

                public static final String WALK = BASE + "walking_";
                public static final int WALK_FRAMES = 6;

                public static final String ATTACK = BASE + "attack_";
                public static final int ATTACK_FRAMES = 6;

                public static final String HURT = BASE + "hurt_";
                public static final int HURT_FRAMES = 6;

                public static final String DEATH = BASE + "explosion_";
                public static final int DEATH_FRAMES = 12;

                public static final String ARM_BASE = BASE+"arms_weapon/";

                public static final String ARM_IDLE = ARM_BASE+"idle_";
                public static final int ARM_IDLE_FRAMES = 6;

                public static final String ARM_WALKING = ARM_BASE+"walking_";
                public static final int ARM_WALKING_FRAMES = 6;

                public static final String ARM_ATTACK = ARM_BASE+"attack_";
                public static final int ARM_ATTACK_FRAMES = 6;

                public static final String ARM_HURT = ARM_BASE+"hurt_";
                public static final int ARM_HURT_FRAMES = 6;
            }
            public static final class Heavy {
                private Heavy() {}
                public static final String BASE = "sprites/enemies/iron_jackals/jackal_heavy/";

                public static final String IDLE = BASE + "idle_";
                public static final int IDLE_FRAMES = 6;

                public static final String WALK = BASE + "walking_";
                public static final int WALK_FRAMES = 6;

                public static final String ATTACK = BASE + "attack_";
                public static final int ATTACK_FRAMES = 6;

                public static final String HURT = BASE + "hurt_";
                public static final int HURT_FRAMES = 6;

                public static final String DEATH = BASE + "explosion_";
                public static final int DEATH_FRAMES = 12;


                public static final String ARM_BASE = BASE+"arms_weapon/";

                public static final String ARM_IDLE = ARM_BASE+"idle_";
                public static final int ARM_IDLE_FRAMES = 12;

                public static final String ARM_WALKING = ARM_BASE+"walking_";
                public static final int ARM_WALKING_FRAMES = 12;

                public static final String ARM_ATTACK = ARM_BASE+"attack_";
                public static final int ARM_ATTACK_FRAMES = 8;

                public static final String ARM_HURT = ARM_BASE+"hurt_";
                public static final int ARM_HURT_FRAMES = 8;
            }
            public static final class HoverDrone {
                private HoverDrone() {}
                public static final String BASE = "sprites/enemies/iron_jackals/jackal_hover_drone/";

                public static final String IDLE = BASE + "idle_";
                public static final int IDLE_FRAMES = 12;

                public static final String WALK = BASE + "flying_";
                public static final int WALK_FRAMES = 12;

                public static final String CHARGE = BASE + "charge_";
                public static final int CHARGE_FRAMES = 6;

                public static final String ATTACK = BASE + "attack_";
                public static final int ATTACK_FRAMES = 8;

                public static final String HURT = BASE + "hurt_";
                public static final int HURT_FRAMES = 8;

                public static final String DEATH = BASE + "explosion_";
                public static final int DEATH_FRAMES = 12;
            }

            public static final class DrillCrawler {
                private DrillCrawler() {}
                public static final String BASE = "sprites/enemies/iron_jackals/jackal_drill_crawler/";

                public static final String ATTACK = BASE + "attack_";
                public static final int ATTACK_FRAMES = 12;

                public static final String DEATH = BASE + "explosion_";
                public static final int DEATH_FRAMES = 12;

                public static final String DRILL = BASE + "drill_";
                public static final int DRILL_FRAMES = 12;

                public static final String HURT = BASE + "hurt_";
                public static final int HURT_FRAMES = 12;

                public static final String IDLE = BASE + "idle_";
                public static final int IDLE_FRAMES = 12;
                
                public static final String WALK = BASE + "walking_";
                public static final int WALK_FRAMES = 12;

                 public static final String PREPARE = BASE + "prepare_";
                public static final int PREPARE_FRAMES = 12;

                public static final String JUMP = BASE + "jump_";
                public static final int JUMP_FRAMES = 12;

                public static final String AIRBORNE = BASE + "airborne_";
                public static final int AIRBORNE_FRAMES = 12;

                public static final String LAND = BASE + "land_";
                public static final int LAND_FRAMES = 12;

                
            }
        }
    }


    public static final class Explosion {
        private Explosion() {}

        public static final String BASE = "sprites/explosion/";

        public static final String GROUND = BASE + "GroundExplo__";
        public static final int GROUND_FRAMES = 11;

        public static final String MID_AIR = BASE + "MidAirExplo__";
        public static final int MID_AIR_FRAMES = 11;
    }


     public static final class Resource {
        private Resource() {}

        public static final String BASE = "sprites/resource/";
        public static final String enemyHealthBar = BASE + "AllHealthBar.png";


    }
    public static final class Audio{
        private Audio(){}

        public static final String BASE = "audio/";

        public static final String MUSIC = BASE+"music/";
        public static final String WEAPONS = BASE+"sfx/weapons/";
        public static final String POWERS = BASE+"sfx/powers/";
        public static final String PLAYER = BASE+"sfx/player/";
        public static final String JETPACK = BASE+"sfx/jetpack/";
        public static final String COMBAT = BASE+"sfx/combat/";
        public static final String ENEMIES = BASE+"sfx/enemies/";
        public static final String WORLD = BASE+"sfx/world/";
        public static final String UI = BASE+"sfx/ui/";

        public static final String GAMEPLAY_TACTICAL = MUSIC+"gameplay_tactical_loop.ogg";
        public static final String GAMEPLAY_ASSAULT = MUSIC+"gameplay_assault_loop.ogg";
        public static final String MENU_ARMORY = MUSIC+"menu_armory_loop.ogg";

        public static String weapon(String id){
            return WEAPONS+id+".wav";
        }

        public static String power(String id){
            return POWERS+id+".wav";
        }

        public static String player(String id){
            return PLAYER+id+".wav";
        }

        public static String jetpack(String id){
            return JETPACK+id+".wav";
        }

        public static String combat(String id){
            return COMBAT+id+".wav";
        }

        public static String enemy(String id){
            return ENEMIES+id+".wav";
        }

        public static String world(String id){
            return WORLD+id+".wav";
        }

        public static String ui(String id){
            return UI+id+".wav";
        }
    }

    public static final class Pause{
        private Pause(){}

        public static final String BASE = "ui/pause/";

        public static final String SHELL = BASE+"pause_shell.png";

        public static final String RESUME_NORMAL = BASE+"resume_normal.png";
        public static final String RESUME_HOVER = BASE+"resume_hover.png";
        public static final String RESUME_PRESSED = BASE+"resume_pressed.png";
        public static final String RESUME_DISABLED = BASE+"resume_disabled.png";

        public static final String RESTART_NORMAL = BASE+"restart_run_normal.png";
        public static final String RESTART_HOVER = BASE+"restart_run_hover.png";
        public static final String RESTART_PRESSED = BASE+"restart_run_pressed.png";
        public static final String RESTART_DISABLED = BASE+"restart_run_disabled.png";

        public static final String SETTINGS_NORMAL = BASE+"settings_normal.png";
        public static final String SETTINGS_HOVER = BASE+"settings_hover.png";
        public static final String SETTINGS_PRESSED = BASE+"settings_pressed.png";
        public static final String SETTINGS_DISABLED = BASE+"settings_disabled.png";

        public static final String MAIN_MENU_NORMAL = BASE+"main_menu_normal.png";
        public static final String MAIN_MENU_HOVER = BASE+"main_menu_hover.png";
        public static final String MAIN_MENU_PRESSED = BASE+"main_menu_pressed.png";
        public static final String MAIN_MENU_DISABLED = BASE+"main_menu_disabled.png";
    }

    public static final class RunSummary{
        private RunSummary(){}

        public static final String BASE = "ui/run_summary/";

        public static final String SHELL = BASE+"run_summary_shell.png";

        public static final String RESTART_NORMAL = BASE+"restart_run_normal.png";
        public static final String RESTART_HOVER = BASE+"restart_run_hover.png";
        public static final String RESTART_PRESSED = BASE+"restart_run_pressed.png";

        public static final String ARMORY_NORMAL = BASE+"return_to_armory_normal.png";
        public static final String ARMORY_HOVER = BASE+"return_to_armory_hover.png";
        public static final String ARMORY_PRESSED = BASE+"return_to_armory_pressed.png";

        public static final String MAIN_MENU_NORMAL = BASE+"main_menu_normal.png";
        public static final String MAIN_MENU_HOVER = BASE+"main_menu_hover.png";
        public static final String MAIN_MENU_PRESSED = BASE+"main_menu_pressed.png";
    }

    public static final class MainMenu{
        private MainMenu(){}

        public static final String BASE = "ui/main_menu/";

        public static final String SHELL = BASE+"main_menu_shell.png";

        public static final String START_NORMAL = BASE+"start_run_normal.png";
        public static final String START_HOVER = BASE+"start_run_hover.png";
        public static final String START_PRESSED = BASE+"start_run_pressed.png";
        public static final String START_DISABLED = BASE+"start_run_disabled.png";

        public static final String ARMORY_NORMAL = BASE+"armory_normal.png";
        public static final String ARMORY_HOVER = BASE+"armory_hover.png";
        public static final String ARMORY_PRESSED = BASE+"armory_pressed.png";
        public static final String ARMORY_DISABLED = BASE+"armory_disabled.png";

        public static final String SETTINGS_NORMAL = BASE+"settings_normal.png";
        public static final String SETTINGS_HOVER = BASE+"settings_hover.png";
        public static final String SETTINGS_PRESSED = BASE+"settings_pressed.png";
        public static final String SETTINGS_DISABLED = BASE+"settings_disabled.png";

        public static final String EXIT_NORMAL = BASE+"exit_normal.png";
        public static final String EXIT_HOVER = BASE+"exit_hover.png";
        public static final String EXIT_PRESSED = BASE+"exit_pressed.png";
        public static final String EXIT_DISABLED = BASE+"exit_disabled.png";
    }

    public static final class Armory{
    private Armory(){}
    public static final String BASE = "ui/armory/";
    public static final String SHELL = BASE+"shell.png";
    public static final String WEAPONS_GRID = BASE+"weapons_grid.png";
    public static final String ICONS_GRID = BASE+"icons_grid.png";
    public static final String CLOSE_NORMAL = BASE+"close_normal.png";
    public static final String CLOSE_HOVER = BASE+"close_hover.png";
    public static final String CLOSE_PRESSED = BASE+"close_pressed.png";
    public static final String CATEGORY_HOVER = BASE+"category_hover.png";
    public static final String CATEGORY_SELECTED = BASE+"category_selected.png";
    public static final String WEAPON_CARD_HOVER = BASE+"weapon_card_hover.png";
    public static final String WEAPON_CARD_SELECTED = BASE+"weapon_card_selected.png";
    public static final String ICON_CARD_HOVER = BASE+"icon_card_hover.png";
    public static final String ICON_CARD_SELECTED = BASE+"icon_card_selected.png";
    public static final String ACTION_BUY = BASE+"action_buy.png";
    public static final String ACTION_BUY_HOVER = BASE+"action_buy_hover.png";
    public static final String ACTION_BUY_PRESSED = BASE+"action_buy_pressed.png";
    public static final String ACTION_EQUIP = BASE+"action_equip.png";
    public static final String ACTION_EQUIP_HOVER = BASE+"action_equip_hover.png";
    public static final String ACTION_EQUIP_PRESSED = BASE+"action_equip_pressed.png";
    public static final String ACTION_OWNED = BASE+"action_owned.png";
    public static final String ACTION_EQUIPPED = BASE+"action_equipped.png";
    public static final String ACTION_INSUFFICIENT_GOLD = BASE+"action_insufficient_gold.png";
    public static final String ACTION_UNAVAILABLE = BASE+"action_unavailable.png";
}

public static final class Settings{

    private Settings(){}

    public static final String BASE = "ui/settings/";
    public static final String PANEL = BASE+"settings_panel.png";
    public static final String SLIDER_FILL = BASE+"slider_fill.png";
    public static final String SLIDER_THUMB_NORMAL = BASE+"slider_thumb_normal.png";
    public static final String SLIDER_THUMB_HOVER = BASE+"slider_thumb_hover.png";
    public static final String SLIDER_THUMB_PRESSED = BASE+"slider_thumb_pressed.png";
    public static final String FULLSCREEN_OFF = BASE+"fullscreen_off.png";
    public static final String FULLSCREEN_OFF_HOVER = BASE+"fullscreen_off_hover.png";
    public static final String FULLSCREEN_ON = BASE+"fullscreen_on.png";
    public static final String FULLSCREEN_ON_HOVER = BASE+"fullscreen_on_hover.png";
    public static final String RESOLUTION_NORMAL = BASE+"resolution_normal.png";
    public static final String RESOLUTION_HOVER = BASE+"resolution_hover.png";
    public static final String RESOLUTION_PRESSED = BASE+"resolution_pressed.png";
    public static final String CONTROLS_NORMAL = BASE+"controls_normal.png";
    public static final String CONTROLS_HOVER = BASE+"controls_hover.png";
    public static final String CONTROLS_PRESSED = BASE+"controls_pressed.png";
    public static final String RESET_NORMAL = BASE+"reset_save_data_normal.png";
    public static final String RESET_HOVER = BASE+"reset_save_data_hover.png";
    public static final String RESET_PRESSED = BASE+"reset_save_data_pressed.png";
    public static final String BACK_NORMAL = BASE+"back_normal.png";
    public static final String BACK_HOVER = BASE+"back_hover.png";
    public static final String BACK_PRESSED = BASE+"back_pressed.png";
}

public static final class Interaction{

    private Interaction(){}

    public static final String BASE = "ui/interaction/";
    public static final String E_INTERACT = BASE+"e_interact.png";
    public static final String E_OPEN = BASE+"e_open.png";
    public static final String E_SHOP = BASE+"e_shop.png";
    public static final String Q_CLAIM = BASE+"q_claim.png";
    public static final String R_RELOAD = BASE+"r_reload.png";
    public static final String RMB_RELOAD = BASE+"rmb_reload.png";
    public static final String RMB_OR_R_RELOAD = BASE+"rmb_or_r_reload.png";
    public static final String LOCKED = BASE+"locked.png";
    public static final String NOT_ENOUGH_GOLD = BASE+"not_enough_gold.png";
    public static final String WEAPON_UNLOCKED = BASE+"weapon_unlocked.png";
    public static final String ITEM_ACQUIRED = BASE+"item_acquired.png";
    public static final String GOLD_GAINED = BASE+"gold_gained.png";
}




}
