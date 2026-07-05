package com.example.mcopt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public final class EntityMerger {

	private EntityMerger() {}

	public static int mergeItemsAndOrbs(ServerWorld world, OptConfig cfg) {
		int merged = 0;
		merged += mergeItems(world, cfg.itemMergeRadius);
		merged += mergeXpOrbs(world, cfg.xpOrbMergeRadius);
		return merged;
	}

	private static int mergeItems(ServerWorld world, double radius) {
		int mergedCount = 0;
		List<ItemEntity> items = new ArrayList<>();
		for (Entity e : world.iterateEntities()) {
			if (e instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
				items.add(itemEntity);
			}
		}

		for (int i = 0; i < items.size(); i++) {
			ItemEntity a = items.get(i);
			if (!a.isAlive()) continue;
			ItemStack stackA = a.getStack();
			if (stackA.isEmpty()) continue;

			for (int j = i + 1; j < items.size(); j++) {
				ItemEntity b = items.get(j);
				if (!b.isAlive()) continue;
				ItemStack stackB = b.getStack();
				if (stackB.isEmpty()) continue;

				if (stackA.getCount() >= stackA.getMaxCount()) break;
				boolean sameItem = stackA.getItem() == stackB.getItem()
						&& ItemStack.areNbtEqual(stackA, stackB);
				if (!sameItem) continue;
				if (a.squaredDistanceTo(b) > radius * radius) continue;

				int space = stackA.getMaxCount() - stackA.getCount();
				int moving = Math.min(space, stackB.getCount());
				if (moving <= 0) continue;

				stackA.increment(moving);
				stackB.decrement(moving);

				if (stackB.isEmpty()) {
					b.discard();
				}
				mergedCount++;
			}
		}
		return mergedCount;
	}

	private static int mergeXpOrbs(ServerWorld world, double radius) {
		int mergedCount = 0;
		List<ExperienceOrbEntity> orbs = new ArrayList<>();
		for (Entity e : world.iterateEntities()) {
			if (e instanceof ExperienceOrbEntity orb && orb.isAlive()) {
				orbs.add(orb);
			}
		}

		for (int i = 0; i < orbs.size(); i++) {
			ExperienceOrbEntity a = orbs.get(i);
			if (!a.isAlive()) continue;

			for (int j = i + 1; j < orbs.size(); j++) {
				ExperienceOrbEntity b = orbs.get(j);
				if (!b.isAlive()) continue;
				if (a.squaredDistanceTo(b) > radius * radius) continue;

				int total = a.getExperienceAmount() + b.getExperienceAmount();
				ExperienceOrbEntity merged = new ExperienceOrbEntity(
						world, a.getX(), a.getY(), a.getZ(), total);
				a.discard();
				b.discard();
				world.spawnEntity(merged);
				mergedCount++;
				break;
			}
		}
		return mergedCount;
	}
}
