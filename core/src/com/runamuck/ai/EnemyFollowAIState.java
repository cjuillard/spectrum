package com.runamuck.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.runamuck.simulation.Entity;
import com.runamuck.simulation.actions.ActionPool;

public enum EnemyFollowAIState implements State<Entity> {
	WAITING() {
		@Override
		public void update(Entity entity) {
			Entity player = entity.getWorld().getPlayerEntity();
			if(player != null) {
				entity.addAction(ActionPool.createFollowAction(entity, player));
				entity.getState().changeState(FOLLOWING);
			}
			
		}
	},
	FOLLOWING() {
		@Override
		public void update(Entity entity) {
			if(entity.getActions().size == 0) {
				entity.getState().changeState(WAITING);
			}
			
		}
	},
	;
	
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
