package com.runamuck.simulation.actions;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.runamuck.simulation.Entity;

public class EntityAction implements Poolable {
	protected Entity source;
	protected boolean complete;
	
	public EntityAction() {
		
	}
	
	public void update(float elapsed) {
		
	}

	@Override
	public void reset() {
		this.source = null;
		complete = false;
	}
	
	public void markComplete() {
		this.complete = true;
	}
	
	public boolean isComplete() {
		return complete;
	}
}
