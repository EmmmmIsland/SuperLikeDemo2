package com.example.superlikedemo.superLike.leonids.modifiers;


import com.example.superlikedemo.superLike.leonids.Particle;

public interface ParticleModifier {

	/**
	 * modifies the specific value of a particle given the current miliseconds
	 * @param particle
	 * @param miliseconds
	 */
	void apply(Particle particle, long miliseconds);

}
