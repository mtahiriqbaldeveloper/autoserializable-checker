# Autoserializable Checker - Usage Guide

## How to Check for @Autoserializable Classes

The plugin provides **three ways** to check for autoserializable classes. Choose the method that fits your workflow:

---

## 1. 📋 Code Inspection (Recommended)

**Best for:** Continuous code quality checking

**How it works:**
- Runs automatically as part of IntelliJ's built-in code inspection
- Shows warnings directly in the editor with yellow underline
- Updates as you type (cached for performance)

**No configuration needed** - Always active!

**What you'll see:**
```
⚠️ This class uses @Autoserializable. Be careful when modifying 
   to maintain serialization compatibility.
```

---

## 2. 🖱️ Manual Action

**Best for:** On-demand checking of specific files

**How to use:**
1. Open any Java file
2. Right-click anywhere in the editor
3. Select **"Check for @Autoserializable"** from the menu
4. View results in a notification balloon

**When to use:**
- Before making changes to a file
- Checking files you're unfamiliar with
- Quick verification during code review

**Example output:**
```
✓ Analysis Complete
File MyClass.java does not contain any @Autoserializable classes.
```

or

```
⚠️ Autoserializable Classes Found
File MyModel.java contains 2 @Autoserializable class(es):
• User
• UserProfile

⚠️ Remember to:
• Maintain backward compatibility
• Update SerialVersionUID if needed
• Document all changes
```

---

## 3. 🔔 Real-time File Notifications (Optional)

**Best for:** Teams with strict serialization policies

**⚠️ Performance note:** Disabled by default - only enable if needed

**How to enable:**
1. Go to **Settings** → **Tools** → **Autoserializable Checker**
2. Check **"Enable real-time file change notifications"**
3. Adjust cooldown period if desired (default: 10 seconds)
4. Click **Apply**

**How it works:**
- Shows notification popup when you modify files containing @Autoserializable
- Includes 1-second debounce to avoid spam during typing
- Cooldown period prevents repeated notifications

**When notifications appear:**
- After you save a file with @Autoserializable classes
- Only if changes were made (not on every save)
- Respects cooldown period between notifications

---

## Performance Comparison

| Method | Performance Impact | When It Runs | Recommended |
|--------|-------------------|--------------|-------------|
| Code Inspection | Very Low (cached) | During normal IDE analysis | ✅ Yes |
| Manual Action | None (on-demand) | Only when you click | ✅ Yes |
| Real-time Notifications | Low-Medium | On every file save | ⚠️ Only if needed |

---

## Recommended Workflow

### For Individual Developers:
1. Let **Code Inspection** catch issues automatically
2. Use **Manual Action** before major refactoring
3. Keep **Real-time Notifications** disabled for best performance

### For Teams with Strict Policies:
1. Enable **Real-time Notifications** to ensure awareness
2. Set cooldown to 30+ seconds to reduce annoyance
3. Still benefit from **Code Inspection** warnings

### For Code Reviews:
1. Run **Manual Action** on files you're reviewing
2. Check for @Autoserializable before approving changes
3. Verify SerialVersionUID updates

---

## Settings

Access plugin settings:
**File** → **Settings** → **Tools** → **Autoserializable Checker**

### Available Settings:
- **Enable real-time file change notifications** (default: OFF)
  - Controls whether FileListener monitors file changes
  - Requires IDE restart to take effect
  
- **Notification cooldown (milliseconds)** (default: 10000)
  - Minimum time between notifications for the same file
  - Only applies when real-time notifications are enabled

---

## Troubleshooting

### "I'm not seeing any warnings"
- Make sure your classes actually use `@Autoserializable` annotation
- Check that Code Inspections are enabled: **Settings** → **Editor** → **Inspections**
- Look for "Autoserializable" in the inspection settings

### "Real-time notifications aren't working"
- Verify setting is enabled in **Settings** → **Tools** → **Autoserializable Checker**
- Restart the IDE after changing the setting
- Check if you're within the cooldown period (default: 10 seconds)

### "Plugin is slowing down my IDE"
- Disable **Real-time Notifications** in settings
- Clear IDE caches: **File** → **Invalidate Caches / Restart**
- The plugin should have minimal impact with notifications disabled

---

## Examples

### Example 1: Simple Check
```java
@Autoserializable
public class User {
    private String name;
    private int age;
}
```
**Result:** Warning appears in editor, Manual Action confirms 1 autoserializable class

### Example 2: Inheritance
```java
@Autoserializable
public class BaseModel { }

public class UserModel extends BaseModel {  // Also detected!
    private String username;
}
```
**Result:** Both classes flagged (inheritance is checked recursively)

### Example 3: Interface Implementation
```java
public class DataObject implements Autoserializable {
    private byte[] data;
}
```
**Result:** Detected via interface implementation check

---

## Best Practices

1. ✅ **Check before modifying** - Run Manual Action before editing unfamiliar @Autoserializable classes
2. ✅ **Update SerialVersionUID** - When making incompatible changes
3. ✅ **Document changes** - Maintain compatibility notes
4. ✅ **Use Code Inspection** - Let IDE warn you automatically
5. ⚠️ **Enable real-time notifications sparingly** - Only if your workflow requires it

---

*For performance details, see PERFORMANCE_IMPROVEMENTS.md*

