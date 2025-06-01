// in game/animation/PunchAnimation.java
package game.animation;

public class PunchAnimation {
    private final float duration;    // total seconds for one punch swing
    private float  elapsed;          // how many seconds have passed since punch started
    private boolean active;          // true if we’re in the middle of a punch

    public PunchAnimation(float durationSeconds) {
        this.duration = durationSeconds;
        this.elapsed = 0f;
        this.active = false;
    }

    /** Call this when the player begins punching */
    public void start() {
        this.elapsed = 0f;
        this.active = true;
    }

    /** Call each frame with deltaTime (in seconds) */
    public void update(float deltaTime) {
        if (!active) return;
        elapsed += deltaTime;
        if (elapsed >= duration) {
            active = false;
            elapsed = duration; // clamp
        }
    }

    /** 
     * Returns a swing‐offset in [0..1], then back to 0:
     * 0 = arm at “rest,” 1 = arm fully forward, then back to 0. 
     */
    public float getProgress() {
        if (!active) return 0f;
        // We want a quick “forward swing” and then a “return.” 
        // For example: first half of duration → forward (0→1), second half → return (1→0).
        float half = duration / 2f;
        if (elapsed < half) {
            return (elapsed / half); // 0→1
        } else {
            return (1f - ((elapsed - half) / half)); // 1→0
        }
    }

    public boolean isActive() {
        return active;
    }
}
