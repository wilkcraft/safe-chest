# 🛡️ Safe-Chest

Safe-Chest is a powerful yet simple Paper plugin that allows players to **protect their chests**, manage access with friends 👥, and prevent unauthorized interactions 🚫.

Perfect for survival servers that want a clean, intuitive chest protection system without complexity.

---

## ✨ Features

- 🔒 Protect chests with custom names  
- 👥 Add or remove friends with access  
- 🧠 Smart double chest detection and support  
- 🚫 Prevent unauthorized access and breaking  
- 💥 Explosion protection for protected chests  
- ⚙️ Hopper interaction control (items can be inserted but not extracted)  
- 📦 GUI to view all your protected chests  
- 💾 Automatic saving and loading (chests.yml)  

---

## 📜 Commands

- `/sc protect <name>` - 🔒 Protect a chest  
- `/sc unprotect <name>` - 🔓 Unprotect a chest  
- `/sc addfriend <player> [chest]` - 👥 Give access to a player  
- `/sc unfriend <player> [chest>` - ❌ Remove access from a player  
- `/sc chests` - 📦 Open GUI with your chests  
- `/sc reload` - 🔄 Reload chests.yml  
- `/sc help` - 📖 Show help menu  

---

## 📦 Installation

1. 📥 Download the latest `.jar` from Modrinth  
2. 📂 Place it in your server's `plugins` folder  
3. ▶️ Start or restart your server  
4. 📝 Configure your chests in: `plugins/Safe-Chest/chests.yml`  

---

## ⚙️ How it works

- 👀 Players protect a chest by looking at it and using a command  
- 💾 The plugin stores:
  - 🆔 Owner UUID  
  - 📍 Chest locations (supports double chests)  
  - 👥 Friend access list  
- 👑 Only the owner can:
  - 📦 Open the chest  
  - ⛏️ Break it  
- 👥 Friends can only:
  - 📦 Open the chest  

---

## 🛡️ Protection System

Safe-Chest prevents:

- ❌ Unauthorized chest opening  
- ❌ Breaking protected chests  
- ❌ Hopper item extraction (items **cannot be taken out**, but **can still be inserted**)  
- ❌ Chest destruction by explosions  
- ❌ Unauthorized double chest connections  

---

## 📝 Notes

- 👤 Only players can use commands  
- ⚙️ Works with Paper 1.21+  
- 📄 Uses YAML storage (no database required)  
- 📦 GUI is read-only (prevents accidental item movement)  

---

## 🚀 Future ideas

- 🔐 Permissions system  
- 🖥️ GUI improvements (click to teleport / manage)  
- ✏️ Chest renaming  
- 🛠️ Admin tools  

---

🚀 Enhance your server security with a lightweight and intuitive chest protection system!
