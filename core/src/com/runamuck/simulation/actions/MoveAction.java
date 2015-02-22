package com.runamuck.simulation.actions;

import com.badlogic.gdx.math.Vector2;
import com.runamuck.simulation.Entity;

public class MoveAction extends EntityAction {

	protected Vector2 dst = new Vector2();
	protected float fudgeDst2;
	
	public MoveAction() {
		super();
	}
	
	protected void init(Entity source, Vector2 dst, float fudgeDst) {
		this.source = source;
		this.dst.set(dst);
		this.fudgeDst2 = fudgeDst * fudgeDst;
	}
	private Vector2 tmp2 = new Vector2();
	
	@Override
	public void update(float elapsed) {
		super.update(elapsed);
		
		tmp2.set(dst).sub(source.getBody().getPosition());
		if(tmp2.len2() <= fudgeDst2) {
			markComplete();
			return;
		}
		tmp2.nor().scl(source.getMoveForce());
		source.getBody().applyForceToCenter(tmp2, true);
	}

	@Override
	public void reset() {
		super.reset();
		
		dst.setZero();
		fudgeDst2 = 0;
	}
}
