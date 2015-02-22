package com.runamuck.simulation.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.runamuck.simulation.Entity;

public class ActionPool {
	
	public static MoveAction createMoveAction(Entity source, Vector2 dst, float fudgeDst) {
		MoveAction action = Pools.obtain(MoveAction.class);
		action.init(source, dst, fudgeDst);
		return action;
	}
	
	public static BusyAction createBusyAction(Entity source) {
		BusyAction action = Pools.obtain(BusyAction.class);
		action.source = source;
		return action;
	}
}
