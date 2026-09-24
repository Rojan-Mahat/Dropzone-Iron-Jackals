package rojan.shooter;
public final class AssetPaths {
    private AssetPaths() {}
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

    public static final class Explosion {
        private Explosion() {}

        public static final String BASE = "sprites/explosion/";

        public static final String GROUND = BASE + "GroundExplo__";
        public static final int GROUND_FRAMES = 11;

        public static final String MID_AIR = BASE + "MidAirExplo__";
        public static final int MID_AIR_FRAMES = 11;
    }
}
