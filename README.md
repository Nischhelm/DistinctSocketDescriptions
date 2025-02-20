# Distinct Socketed Descriptions
 Socketed Expansion for DDD


All Effects can be used both with DDDAttackingActivators as well as DDDAttackedActivators, but it doesn't always make sense to do so

#### Example: DDDResistanceEffect

- if it runs on DDD Attacked, it will modify the defending players resistances, so you can for example add +50% fire dmg resistance
- if it runs on DDD Attacking, it will modify the defending targets resistances, so you can for example reduce the mobs fire resistance by -50%

#### Example: DDDDamageEffect

Here it doesn't make sense to use certain Effects with certain Activators, even though it is possible

- if it runs on DDD Attacking, it will modify the attacking players damage type amounts, so you can for example add +5 fire damage
- if it runs on DDD Attacked, it will modify the attacking mobs damage amounts, so you could for example reduce their fire damage by -5, which is a weird approach. Recommended is using DDD Resistance for that.