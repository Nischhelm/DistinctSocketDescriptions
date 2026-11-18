# Distinct Socketed Descriptions
 Socketed Expansion for DDD

## Effects

| Effect Type | Fields | Description |
|---|---|---|
| `DDD Damage` | Damage Type, Amount, Operation | Modifies the damage of a damage type, see Operation |
| `DDD Resistance` | Damage Type, Amount | Adds resistance to a damage type (0.5 = +50%, negative values reduce resistance) |
| `DDD Apply Immunity` | Damage Type | Adds immunity to a damage type |
| `DDD Remove Immunity` | Damage Type | Removes immunity to a damage type |

All effects are Socketed activatable effects, so they also take:

- `Equipment Slot Type`: required, the Socketed slot type of the effect
- `Damage Type`: required, any registered DDD damage type, for example `ddd_fire`
- `Activator`: required, `DDD Attacking` or `DDD Attacked`, see Activators
- `Targets`: required, `Self` or `Other` depending on Activator and effect, see Targets
- `Tooltip Key`: optional, replaces the generated tooltip

## Activators

| Activator Type | Description |
|---|---|
| `DDD Attacking` | Activates when the player attacks something |
| `DDD Attacked` | Activates when the player gets damaged, also without a living attacker, for example by lava |

Both activate while DDD determines the damage and gathers the defenses of a hit, and take:

- `Directly Activated`: optional, default `false`
  - `false`: the effect is active as long as the item is equipped in a valid slot
  - `true`: the effect only activates from the item that is used: the main hand weapon when attacking, or the hit armor pieces when attacked
- `Condition`: optional, any Socketed condition, for example `Chance`

Like Socketed's attack activators, the attacker is the immediate source for melee attacks and the true source for ranged attacks, so attacks of pets don't activate the effects of their owner.

DDD effects only work with these activators, and these activators only activate DDD effects.

## Targets

Damage effects modify the attacker's damage, the other effects modify the defender's resistances and immunities. The target has to be the entity that gets modified:

| Effect | Activator | Target | What it does |
|---|---|---|---|
| `DDD Damage` | `DDD Attacking` | `Self` | Modifies the player's damage, for example +5 fire damage or turn 30% of the damage to fire |
| `DDD Damage` | `DDD Attacked` | `Other` | Modifies the attacking mob's damage, for example -5 fire damage. Recommended is using DDD Resistance for that. Can't be directly activated |
| `DDD Resistance` | `DDD Attacked` | `Self` | Modifies the player's resistances, for example +50% fire resistance |
| `DDD Resistance` | `DDD Attacking` | `Other` | Modifies the target's resistances, for example -50% fire resistance |
| `DDD Apply/Remove Immunity` | `DDD Attacked` | `Self` | Adds/removes immunities of the player |
| `DDD Apply/Remove Immunity` | `DDD Attacking` | `Other` | Adds/removes immunities of the target, for example remove a mob's fire immunity |

Any other target never affects anything, which gets logged as a warning.

Example:

```json
{
  "Effect Type": "DDD Damage",
  "Equipment Slot Type": "HAND",
  "Damage Type": "ddd_fire",
  "Amount": "[3 : 5]",
  "Operation": "ADD",
  "Activator": {"Activator Type": "DDD Attacking", "Directly Activated": true, "Condition": {"Condition Type": "Chance", "Chance": 0.5}},
  "Targets": [{"Target Type": "Self"}]
}
```

`DDD Damage` additionally takes:

- `Operation`: optional, default `ADD`

| Operation | Amount | Example Tooltip | What it does |
|---|---|---|---|
| `ADD` | flat damage | +5 Fire Damage | Adds the amount to the damage of the type |
| `MULTIPLY_EXTRA` | 0.5 = 50% | +50% Total Damage as Fire | Adds the amount times the total damage as damage of the type |
| `CONVERT` | 0.3 = 30% | Turn 30% Damage to Fire | Turns the amount of the damage of every other type into the type, the total damage stays the same |
| `MULTIPLY_TYPE` | factor, 1.5 = x1.5 | x1.5 Fire Damage | Multiplies the damage of the type, does nothing if the hit doesn't deal damage of that type |

All damage effects active in one hit (from the attacker and the defender) get applied together in a fixed order, so the order of gems doesn't matter:

1. `CONVERT`: all converts read the damage from before any conversion. Converts to the same type add up. A type never converts to itself, so "Turn 30% Damage to Fire" leaves existing fire damage alone and takes 30% of every other type. If the converts a type gives up add up to more than 100% (for example 80% to Fire and 60% to Cold), they get scaled down proportionally (57% Fire, 43% Cold), so no damage gets created or lost
2. `ADD` and `MULTIPLY_EXTRA`: `MULTIPLY_EXTRA` uses the total damage after conversion and before any `ADD` or `MULTIPLY_EXTRA`, so multiple `MULTIPLY_EXTRA` effects don't feed into each other
3. `MULTIPLY_TYPE`: multipliers of the same type add up, two x1.5 make x2

Damage of a type can't go below 0. `CONVERT` and `MULTIPLY_TYPE` don't allow negative amounts.

## FirstAid

With FirstAid installed, directly activated armor effects only activate from the armor pieces of the body parts FirstAid actually distributes damage to.
