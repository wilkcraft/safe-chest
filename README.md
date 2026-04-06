# 🛡️ Safe-Chest

**Safe-Chest** is a powerful yet simple Paper plugin that allows players to protect their chests, manage access with friends, and prevent unauthorized interactions.

Perfect for survival servers that want a clean, intuitive chest protection system without complexity.

## ✨ Features

- 🔒 Protect chests with custom names
- 👥 Add or remove friends with access
- 🧠 Smart double chest detection and support
- 🚫 Prevent unauthorized access and breaking
- 💥 Explosion protection for protected chests
- ⚙️ Hopper interaction control (items can be inserted but not extracted)
- 📦 GUI to view all your protected chests
- 💾 Automatic saving and loading (`chests.yml`)

## 📜 Commands

| Command | Description |
|---------|-------------|
| `/sc protect <name>` | 🔒 Protect a chest |
| `/sc unprotect <name>` | 🔓 Unprotect a chest |
| `/sc addfriend <player> [chest]` | 👥 Give access to a player |
| `/sc unfriend <player> [chest]` | ❌ Remove access from a player |
| `/sc chests` | 📦 Open GUI with your chests |
| `/sc reload` | 🔄 Reload `chests.yml` |
| `/sc help` | 📖 Show help menu |

### Examples

```
/sc protect MyTreasure
/sc addfriend Steve
/sc addfriend Alex MyTreasure
/sc unfriend Steve
/sc chests
```

## 📦 Installation

1. 📥 Download the latest `.jar` from Modrinth
2. 📂 Place it in your server's `plugins` folder
3. ▶️ Start or restart your server
4. 📝 Your chests will be saved in `plugins/Safe-Chest/chests.yml`

## ⚙️ How It Works

- 👀 Players protect a chest by looking at it and using `/sc protect <name>`
- 💾 The plugin stores:
  - 🆔 Owner UUID
  - 📍 Chest locations (supports double chests)
  - 👥 Friend access list
- 👑 Only the owner can:
  - 📦 Open the chest
  - ⛏️ Break it
- 👥 Friends can only:
  - 📦 Open the chest

## 🛡️ Protection System

Safe-Chest prevents:

- ❌ Unauthorized chest opening
- ❌ Breaking protected chests
- ❌ Hopper item extraction (items **cannot be taken out**, but **can still be inserted**)
- ❌ Chest destruction by explosions
- ❌ Unauthorized double chest connections

## 🔧 Configuration

The plugin uses a simple YAML storage system. No manual configuration is needed, but you can edit:

```
plugins/Safe-Chest/chests.yml
```

Example entry:

```yaml
chests:
  world,x100,y64,z200:
    name: "MyTreasure"
    owner: "uuid-of-player"
    friends:
      - "uuid-of-friend1"
      - "uuid-of-friend2"
    isDoubleChest: false
```

## 📝 Notes

- 👤 Only players can use commands (not console)
- ⚙️ Works with Paper 1.21+ and Java 21
- 📄 Uses YAML storage (no database required)
- 📦 GUI is read-only (prevents accidental item movement)
- 🧠 Double chests are automatically detected and protected as a single unit

## 🛠️ Building from Source

This project uses Maven.

```
git clone https://github.com/yourusername/Safe-Chest.git
cd Safe-Chest
mvn clean package
```

The compiled plugin will appear in:

```
target/Safe-Chest-1.2.6.jar
```

## 🚀 Future Ideas

- 🔐 Permissions system for finer control
- 🖥️ GUI improvements (click to teleport / manage chests)
- ✏️ Chest renaming without unprotecting
- 🛠️ Admin tools to manage all protected chests
- 📊 Database support (MySQL) for larger servers

## 👤 Author

Developed by **Wilkcraft**

## 📄 License

This project is open-source. You may modify and distribute it following the repository license.

---

🚀 Enhance your server security with a lightweight and intuitive chest protection system!
