package com.runamuck.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.SpectrumWorld;
import com.runamuck.simulation.actions.ActionPool;

public enum RandomMoveAIState implements State<Entity> {
	MOVING,
	;
	
	private Vector2 tmp2 = new Vector2();
	@Override
	public void update(Entity entity) {
		if(entity.getActions().size == 0) {
			SpectrumWorld world = entity.getWorld();
			tmp2.set(MathUtils.random(-world.getWidth() / 2f, world.getWidth() / 2f),
					MathUtils.random(-world.getHeight() / 2f, world.getHeight() / 2f));
			float fudgeDst = world.getWidth() / 100f;
			entity.addAction(ActionPool.createMoveAction(entity, tmp2, fudgeDst));
		}
		
	}
	
	@Override
	public void enter(Entity entity) {
		
	}


	@Override
	public void exit(Entity entity) {
		
	}

	@Override
	public boolean onMessage(Entity entity, Telegram telegram) {
		return false;
	}
}
