# Speed Force Mod

A Minecraft mod that adds Speed Force abilities inspired by The Flash.

## Features

### Obtaining Speed Force
1. **Lightning Strike** - When poisoned and struck by lightning, 30% chance to gain Speed Force
2. **Command** - Use `/speedforce grant [player] [level]` or `/speedforce revoke [player]`
3. **Particle Accelerator** - Right-click the block to activate, 100% chance to gain Speed Force

### Speed Force Abilities
| Speed Level | Ability |
|-------------|---------|
| 1+ | High-speed movement, Health regeneration, Lightning trail when sprinting |
| 3+ | Water walking (while sprinting) |
| 4+ | Wall running (while sprinting) |
| 5+ | Speed backlash (catches fire while sprinting - can be prevented with full Flash Suit) |

### Skills
- **Phase Through Walls** (V key) - Pass through solid blocks
- **Bullet Time** (B key) - Slow down nearby entities and projectiles (requires Speed Level 3+, consumes hunger)

### Flash Suit
Crafted with Leather + Iron Ingots

| Piece | Effect |
|-------|--------|
| Helmet | Night Vision |
| Chestplate | Speed Level +1 |
| Leggings | - |
| Boots | Water walking (no speed level requirement) |

**Full Set Bonus:**
- Immunity to speed backlash (fire)
- 50% reduced hunger consumption for Bullet Time

## Crafting Recipes

### Particle Accelerator
```
 I 
IRI
 I 
I = Iron Ingot, R = Redstone Block
```

### Flash Suit
Each piece uses Leather (L) and Iron Ingot (I) in armor-shaped patterns.

## Commands
- `/speedforce grant [player] [level]` - Grant Speed Force to a player
- `/speedforce revoke [player]` - Remove Speed Force from a player
- `/speedforce info` - Check your current Speed Force status

## Building
```bash
./gradlew build
```

## Requirements
- Minecraft 1.21.1
- NeoForge 21.1.77+
- Java 21