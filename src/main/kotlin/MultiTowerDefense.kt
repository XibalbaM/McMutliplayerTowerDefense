package fr.xibalba.minecraft.mutliplayertowerdefense

import net.fabricmc.api.ModInitializer

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.MobEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.Identifier
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.Registry
import java.util.Random


class MultiTowerDefense : ModInitializer {

    override fun onInitialize() {
    }

package net.faltaix.towermod


object MultiDefenseMod : ModInitializer {
	const val MOD_ID = "towerdefense"

	private val towers = mutableListOf<Tower>()
	private val enemies = mutableListOf<MobEntity>()
	private var wave = 1
	private var currency = 100

	override fun onInitialize() {
		println("Tower Defense Mod Loaded!")
		ServerTickEvents.END_WORLD_TICK.register { world: ServerWorld -> tick(world) }
		initializeTowers()
	}

	private fun tick(world: ServerWorld) {
		if (enemies.isEmpty()) {
			spawnWave(world)
		}
		towers.forEach { it.attack(world, enemies) }
		enemies.removeIf { it.isDead }
	}

	private fun initializeTowers() {
		towers.add(Tower(EntityType.SKELETON, 5, "flèche")) // Tour de type squelette
		towers.add(Tower(EntityType.ZOMBIE, 10, "ralentissement")) // Tour de type zombie
		towers.add(Tower(EntityType.BLAZE, 15, "explosion")) // Tour de type blaze
	}

	private fun spawnWave(world: ServerWorld) {
		println("Début de la vague $wave")
		repeat(wave * 2) {
			val enemyType = if (Random().nextBoolean()) EntityType.ZOMBIE else EntityType.SKELETON
			val enemy = enemyType.create(world)
			if (enemy is MobEntity) { // Vérifie que l'entité est valide
				enemy.refreshPositionAndAngles(0.0, 100.0, 0.0, 0f, 0f) // Positionner l'ennemi
				world.spawnEntity(enemy) // Spawn dans le monde
				enemies.add(enemy) // Ajouter à la liste des ennemis
			} else {
				println("Erreur : Impossible de créer l'entité ${enemyType.name}")
			}
		}
		wave++
	}

	fun buyTower(type: EntityType<out MobEntity>, effect: String, cost: Int) {
		if (currency >= cost) {
			towers.add(Tower(type, 1, effect))
			currency -= cost
			println("Tour achetée: ${type.name} avec effet $effect")
		} else {
			println("Pas assez d'argent!")
		}
	}
}

class Tower(private val entityType: EntityType<out MobEntity>, private var level: Int, private val effect: String) {
	fun attack(world: ServerWorld, enemies: MutableList<MobEntity>) {
		if (enemies.isNotEmpty()) {
			val target = enemies.random()
			println("${entityType.name} attaque ${target.type.name} avec effet $effect au niveau $level!")
			applyEffect(target)
		}
	}

	fun upgrade() {
		level++
		println("Tour ${entityType.name} améliorée au niveau $level!")
	}

	private fun applyEffect(target: MobEntity) {
		when (effect) {
			"flèche" -> println("Tir d'une flèche sur ${target.type.name}")
			"ralentissement" -> println("Ralentissement appliqué sur ${target.type.name}")
			"explosion" -> println("Explosion touchant ${target.type.name}")
		}
	}
}

}
