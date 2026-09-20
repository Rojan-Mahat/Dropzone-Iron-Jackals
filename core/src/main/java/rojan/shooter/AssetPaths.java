package rojan.shooter;

public final class AssetPaths {
    private AssetPaths() {}
    public static final class Player {
        private Player() {}
        public static final class Unarmed {
            private Unarmed() {}
            public static final String IDLE = "sprites/player/unarmed/idle.png";
            public static final String RUNNING = "sprites/player/unarmed/running.png";
            public static final String CROUCH_IDLE = "sprites/player/unarmed/crouch_idle.png";
            public static final String CROUCH_WALKING = "sprites/player/unarmed/crouch_walking.png";
            public static final String AIRBORNE = "sprites/player/unarmed/airborne.png";
            public static final String JET_THRUST = "sprites/player/unarmed/jet_thrust.png";
            public static final String HIT = "sprites/player/unarmed/hit.png";
            public static final String DEATH = "sprites/player/unarmed/death.png";
        }

        public static final class Armed {
            private Armed() {}
            public static final class Rifle {
                private Rifle() {}
                public static final String IDLE = "sprites/player/armed/rifle/idle.png";

                // Add later:
                //
                // RUNNING
                // CROUCH
                // AIRBORNE
                // JET_THRUST
            }


            public static final class Pistol {
                private Pistol() {}
                // Future pistol stance assets
            }


            public static final class Heavy {
                private Heavy() {}
                // Future rocket/heavy stance assets
            }


            public static final class Melee {
                private Melee() {}
                // Future melee stance assets
            }
        }


        // ---------------- ACTIONS ----------------
        public static final class Actions {
            private Actions() {}
            // Future:
            // THROW
            // RELOAD
            // etc.
        }
        public static final String AIM_UP = "sprites/player/aim_up.png";
        public static final String AIM_DOWN = "sprites/player/aim_down.png";
        public static final String RECOIL = "sprites/player/recoil.png";
    }


    public static final class Weapons {
        private Weapons() {}
        public static final class Rifles {
            private Rifles() {}
            public static final String AR = "sprites/weapons/rifles/ar.png";
            public static final String AR_BULLET = "sprites/weapons/rifles/ar_bullet.png";
            public static final String SMG = "sprites/weapons/rifles/smg.png";
            public static final String SMG_BULLET = "sprites/weapons/rifles/smg_bullet.png";
            public static final String SNIPER = "sprites/weapons/rifles/sniper.png";
            public static final String SNIPER_BULLET = "sprites/weapons/rifles/sniper_bullet.png";
            public static final String SHOTGUN = "sprites/weapons/rifles/shotgun.png";
            public static final String SHOTGUN_BULLET = "sprites/weapons/rifles/shotgun_bullet.png";
        }

        public static final class Pistol {
            private Pistol() {}
            public static final String PISTOL = "sprites/weapons/pistol/pistol.png";

            public static final String BULLET = "sprites/weapons/pistol/pistol_bullet.png";
        }

        public static final class Heavy {
            private Heavy() {}
            public static final String ROCKET_LAUNCHER = "sprites/weapons/heavy/rocket_launcher.png";
            public static final String ROCKET_BULLET = "sprites/weapons/heavy/rocket_bullet.png";
        }

        public static final class Melee {
            private Melee() {}
            public static final String KNIFE = "sprites/weapons/melee/knife.png";
        }

        public static final class Throwables {
            private Throwables() {}
            public static final String GRENADE = "sprites/weapons/throwables/grenade.png";
            public static final String EMP_GRENADE = "sprites/weapons/throwables/emp_grenade.png";
            public static final String POISON_GRENADE = "sprites/weapons/throwables/poison_grenade.png";
            public static final String GLOO_WALL = "sprites/weapons/throwables/gloo_wall.png";
        }


        public static final class Projectiles {
            private Projectiles() {}
            public static final String GRENADE = "sprites/weapons/projectile/grenade.png";
            public static final String ASSAULT_BULLET = "sprites/weapons/projectile/as_bullet.png";
            public static final String GROUND_GUNNER_BULLET = "sprites/weapons/projectile/gg_bullet.png";
            public static final String EMP = "sprites/weapons/projectile/emp.png";
            public static final String POISON = "sprites/weapons/projectile/poison.png";
            public static final String GLOO_WALL = "sprites/weapons/projectile/gloo_wall.png";
        }
    }


    public static final class Enemies {
        private Enemies() {}
        public static final class GroundGunner {
            private GroundGunner() {}
            public static final String IDLE = "sprites/enemy/ground_gunner/idle.png";
            public static final String WALKING = "sprites/enemy/ground_gunner/walking.png";
            public static final String ATTACK = "sprites/enemy/ground_gunner/attack.png";
            public static final String HIT = "sprites/enemy/ground_gunner/hit.png";
            public static final String DEATH = "sprites/enemy/ground_gunner/death.png";
        }


        public static final class AirStalker {
            private AirStalker() {}
            public static final String IDLE = "sprites/enemy/air_stalker/idle.png";
            public static final String FLYING = "sprites/enemy/air_stalker/flying.png";
            public static final String ATTACK = "sprites/enemy/air_stalker/attack.png";
            public static final String HIT = "sprites/enemy/air_stalker/hit.png";
            public static final String DEATH = "sprites/enemy/air_stalker/death.png";
        }


        public static final class PhaseMiner {
            private PhaseMiner() {}
            public static final String IDLE = "sprites/enemy/phase_miner/idle.png";
            public static final String DRILL = "sprites/enemy/phase_miner/drill.png";
            public static final String ATTACK = "sprites/enemy/phase_miner/attack.png";
            public static final String HIT = "sprites/enemy/phase_miner/hit.png";
            public static final String DEATH = "sprites/enemy/phase_miner/death.png";
        }
    }

    public static final class Effects {
        private Effects() {}
        public static final String FLAME = "sprites/effects/flame.png";
        public static final String SPARK = "sprites/effects/spark.png";
        public static final String POISON = "sprites/effects/poison.png";
        public static final String GRENADE_EXPLOSION = "sprites/effects/grenade_explosion.png";
        public static final String GLOO_WALL_FORMING = "sprites/effects/gloo_wall_forming.png";
        public static final String GLOO_WALL_BREAKING = "sprites/effects/gloo_wall_breaking.png";
    }


    public static final class Powerups {
        private Powerups() {}
        public static final String HEALTH = "sprites/powerups/health_pickup.png";
        public static final String AMMO = "sprites/powerups/ammo_crate.png";
        public static final String SHIELD = "sprites/powerups/shield_pickup.png";
        public static final String JETPACK_BOOSTER = "sprites/powerups/jetpack_booster.png";
        public static final String RAPID_FIRE = "sprites/powerups/rapid_fire.png";
        public static final String INVULNERABILITY = "sprites/powerups/invulnerability.png";
        public static final String DAMAGE_BOOSTER = "sprites/powerups/damage_booster.png";
        public static final String WALL_PHASE = "sprites/powerups/wall_phase.png";
    }


    public static final class Map {
        private Map() {}
        public static final String ALIEN_RUINS = "map/world/AlienRuins.tmx";
        public static final String BACKGROUND = "map/background/background.png";
        public static final String BACKGROUND_BAKED = "map/background/background_baked.png";
    }


    public static final class References {
        private References() {}
        public static final String PLAYER = "references/playerReference.png";
        public static final String GROUND_GUNNER = "references/ground_gunner.png";
        public static final String AIR_STALKER = "references/air_stalker.png";
        public static final String PHASE_MINER = "references/phase_miner.png";
    }
}