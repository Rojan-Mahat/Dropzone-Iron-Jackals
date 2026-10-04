package rojan.shooter;
public final class AssetPaths {
    private AssetPaths() {}

    public static class Hud{
        public static final String PLAYER_HEALTH_FRAME = "ui/hud/player_health_frame.png";
        public static final String PLAYER_HEALTH_FILL = "ui/hud/player_health_fill.png";
        public static final String JETPACK_FRAME = "ui/hud/jetpack_frame.png";
        public static final String JETPACK_FILL = "ui/hud/jetpack_fill.png";
        public static final String AMMO_PANEL = "ui/hud/ammo_panel.png";
        public static final String SCOPE_OVERLAY = "ui/hud/scope_overlay.png";
    }

    public static final class EnemyProjectile {
        private EnemyProjectile() {}
        public static final String BASE = "sprites/enemy_projectiles/";
        public static final String HEAVY_PROJECTILE= BASE + "heavy.png";
        public static final String RIFLEMAN_PROJECTILE= BASE + "rifleman.png";
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

    }

    public static final class Data {
        public static final String SOLDIER1_GRIPS = "data/player1/soldier1_grips.json";
    }

    public static final class Weapons {
        private Weapons() {}
        public static final class Rifles {
            private Rifles() {}
            public static final String AR = "sprites/guns/rifle/rifle.png";
        }
    }

    public static final class Bullets {
        private Bullets() {}
        
        public static final String BASE = "sprites/bullet/";

        public static final String ORANGE_SPIN = BASE + "OrangeSpin__";
        public static final int ORANGE_SPIN_FRAMES = 10;

        public static final String ORANGE_SCALE = BASE + "OrangeScale__";
        public static final int ORANGE_SCALE_FRAMES = 10;

        public static final String YELLOW_SPIN = BASE + "YellowSpin__";
        public static final int YELLOW_SPIN_FRAMES = 10;

        public static final String YELLOW_SCALE = BASE + "YellowScale__";
        public static final int YELLOW_SCALE_FRAMES = 10;

        public static final String GRENADE = BASE + "Grenade__";
        public static final int GRENADE_FRAMES = 10;
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





}
