import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private List<Particle> particles;
    private Random random;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
    }
    
    public void update() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update();
            if (particle.isDead()) {
                iterator.remove();
            }
        }
    }
    
    public void draw(Graphics2D g2d) {
        for (Particle particle : particles) {
            particle.draw(g2d);
        }
    }
    
    public void createExplosion(int x, int y, Color color, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = random.nextDouble() * 5 + 2;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            particles.add(new Particle(x, y, vx, vy, color, 1000 + random.nextInt(500)));
        }
    }
    
    public void createPowerUpEffect(int x, int y, Color color) {
        for (int i = 0; i < 15; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = random.nextDouble() * 3 + 1;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 2;
            
            particles.add(new Particle(x, y, vx, vy, color, 800 + random.nextInt(400)));
        }
    }
    
    public void createEngineSmoke(int x, int y) {
        if (random.nextDouble() < 0.7) {
            double vx = (random.nextDouble() - 0.5) * 2;
            double vy = random.nextDouble() * 2 + 1;
            Color smokeColor = new Color(100, 100, 100, 150);
            
            particles.add(new SmokeParticle(x, y, vx, vy, smokeColor, 500 + random.nextInt(300)));
        }
    }
    
    public void clear() {
        particles.clear();
    }
    
    private static class Particle {
        protected double x, y, vx, vy;
        protected Color color;
        protected long lifeTime, maxLifeTime;
        protected int size;
        
        public Particle(double x, double y, double vx, double vy, Color color, long maxLifeTime) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.maxLifeTime = maxLifeTime;
            this.lifeTime = 0;
            this.size = 4;
        }
        
        public void update() {
            x += vx;
            y += vy;
            vy += 0.1;
            lifeTime += 16;
        }
        
        public void draw(Graphics2D g2d) {
            float alpha = 1.0f - (float) lifeTime / maxLifeTime;
            alpha = Math.max(0, Math.min(1, alpha));
            
            Color drawColor = new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int) (alpha * 255)
            );
            
            g2d.setColor(drawColor);
            g2d.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }
        
        public boolean isDead() {
            return lifeTime >= maxLifeTime;
        }
    }
    
    private static class SmokeParticle extends Particle {
        public SmokeParticle(double x, double y, double vx, double vy, Color color, long maxLifeTime) {
            super(x, y, vx, vy, color, maxLifeTime);
            this.size = 6;
        }
        
        @Override
        public void update() {
            super.update();
            size += 0.1;
            vx *= 0.98;
            vy *= 0.98;
        }
    }
}
