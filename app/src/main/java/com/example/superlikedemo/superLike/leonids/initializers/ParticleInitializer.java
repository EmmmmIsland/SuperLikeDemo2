package com.example.superlikedemo.superLike.leonids.initializers;


import com.example.superlikedemo.superLike.leonids.Particle;

import java.util.Random;



public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
