package com.runamuck.data;

import com.runamuck.simulation.EntityType;

public class EntityDefinition {
	public static float getWidth(EntityType type) {
		switch(type) {
		case PLAYER:
			return 2;
		}
		
		return 1;
	}
	
	public static float getHeight(EntityType type) {
		switch(type) {
		case PLAYER:
			return 2;
		}
		
		return 1;
	}
}
