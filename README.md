# Distinct Socketed Descriptions
 Socketed Expansion for DDD

## Effects

| Effect Type | Fields | Description |
|---|---|---|
| `DDD Damage` | Damage Type, Amount | Adds flat damage of a damage type |
| `DDD Resistance` | Damage Type, Amount | Adds resistance to a damage type (0.5 = +50%, negative values reduce resistance) |
| `DDD Apply Immunity` | Damage Type | Adds immunity to a damage type |
| `DDD Remove Immunity` | Damage Type | Removes immunity to a damage type |

All effects also take the shared fields:

- `Equipment Slot Type`: required, the Socketed slot type of the effect
- `Damage Type`: required, any registered DDD damage type, for example `ddd_fire`
- `Directly Activated`: optional, default `false`
  - `false`: the effect is active as long as the item is equipped in a valid slot
  - `true`: the effect only activates from the item that is used: the main hand weapon when attacking, or the hit armor pieces when attacked
- `Activation`: optional, `ATTACKING` or `ATTACKED`
  - `ATTACKING`: applies when the player attacks
  - `ATTACKED`: applies when the player gets attacked
  - If not set, it depends on the slot type: hand slots (`HAND`, `MAINHAND`, `OFFHAND`) are `ATTACKING`, body slots (`BODY`, `HEAD`, `CHEST`, `LEGS`, `FEET`) are `ATTACKED`. Any other slot type (for example `ALL`) uses the effects default: `ATTACKING` for DDD Damage and DDD Remove Immunity, `ATTACKED` for DDD Resistance and DDD Apply Immunity

Directly activated effects only ever run from the hand when attacking and from the body when attacked, so their Activation should match their slot type.

## What each effect does depending on Activation

It doesn't always make sense to use every effect with every activation, even though it is possible.

#### DDD Resistance

- `ATTACKED`: modifies the defending players resistances, so you can for example add +50% fire damage resistance
- `ATTACKING`: modifies the attacked targets resistances, so you can for example reduce the mobs fire resistance by -50%

#### DDD Damage

- `ATTACKING`: modifies the attacking players damage type amounts, so you can for example add +5 fire damage
- `ATTACKED`: modifies the attacking mobs damage amounts, so you could for example reduce their fire damage by -5, which is a weird approach. Recommended is using DDD Resistance for that. This only works for non directly activated effects

#### DDD Apply Immunity / DDD Remove Immunity

- `ATTACKED`: adds/removes immunities of the defending player
- `ATTACKING`: adds/removes immunities of the attacked target, so you can for example remove a mobs fire immunity

## FirstAid

With FirstAid installed, directly activated armor effects only activate from the armor pieces of the body parts FirstAid actually distributes damage to.
