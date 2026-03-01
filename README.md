# BanAnimations
- For issues with the plugin, join https://discord.gg/g9RvFwgESy
- or create a new Issue: https://github.com/Matt4499/BanAnimations/issues/new  

- For help using this plugin visit the wiki: https://github.com/Matt4499/BanAnimations/wiki

## Punish GUI Duration Config

Durations in the punish GUI are fully configurable with `punish_gui.duration_options`.
You can add/remove any amount of durations, change materials, and choose GUI slots.

Each entry supports:
- `name` (display name)
- `time` (value passed into `%time%` / `%duration%` placeholders)
- `material`
- `slot`
- optional `lore`

### Permanent-only example

```yml
punish_gui:
	duration_options:
		- name: '&cPermanent'
			time: 'permanent'
			material: 'BEDROCK'
			slot: 22
			lore:
				- '&7Use permanent punishment.'
```

### Larger custom example

```yml
punish_gui:
	duration_options:
		- name: '&f15 Minutes'
			time: '15m'
			material: 'CLOCK'
			slot: 10
		- name: '&f30 Minutes'
			time: '30m'
			material: 'CLOCK'
			slot: 11
		- name: '&f1 Hour'
			time: '1h'
			material: 'CLOCK'
			slot: 12
		- name: '&f6 Hours'
			time: '6h'
			material: 'CLOCK'
			slot: 13
		- name: '&cPermanent'
			time: 'permanent'
			material: 'BEDROCK'
			slot: 22
```