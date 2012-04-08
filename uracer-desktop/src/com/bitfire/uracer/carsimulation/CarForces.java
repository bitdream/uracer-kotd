package com.bitfire.uracer.carsimulation;

/** Represents the forces computed by the car simulator.
 *
 * @author manuel */

public final class CarForces {
	public float velocity_x;
	public float velocity_y;
	public float angularVelocity;

	public CarForces() {
		reset();
	}

	public CarForces( CarForces other ) {
		set( other );
	}

	public void reset() {
		velocity_x = 0;
		velocity_y = 0;
		angularVelocity = 0;
	}

	public CarForces set( CarForces other ) {
		this.velocity_x = other.velocity_x;
		this.velocity_y = other.velocity_y;
		this.angularVelocity = other.angularVelocity;
		return this;
	}

	public CarForces newCopy() {
		CarForces f = new CarForces( this );
		return f;
	}
}
