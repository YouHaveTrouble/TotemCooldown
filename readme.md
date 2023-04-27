# TotemCooldown

A simple plugin that lets you configure the cooldown of totems of undying.

Cooldown persists through logouts and server restarts, so it cannot be skipped.

## Config
```yaml
#amount of ticks to set the cooldown to (1 second = 20 ticks)
totem-cooldown-ticks: 100 

#whether to reset the cooldown on death
resets-on-death: true
```