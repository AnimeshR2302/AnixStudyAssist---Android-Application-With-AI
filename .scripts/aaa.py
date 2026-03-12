import os
import shutil
import re

def refactor_project():
    root_dir = rf"D:\Android\AndroidProjects\AnixStudyAssist"

    # Define mappings
    replacements = {
        "com.anix.android.anixstudyassist": "com.anix.android.anixstudyassist",
        "com.anix.android": "com.anix.android",
        "AnixStudyAssist": "AnixStudyAssist",
        "anixstudyassist": "anixstudyassist"
    }

    print("Step 1: Updating file contents and renaming files...")
    for dirpath, dirnames, filenames in os.walk(root_dir, topdown=False):
        if ".git" in dirpath or ".gradle" in dirpath or ".idea" in dirpath or "build" in dirpath:
            continue

        for filename in filenames:
            file_path = os.path.join(dirpath, filename)

            # 1. Update file content
            try:
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                new_content = content
                for old, new in replacements.items():
                    new_content = new_content.replace(old, new)

                if new_content != content:
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
            except Exception as e:
                print(f"Could not process content of {file_path}: {e}")

            # 2. Rename the file itself if it contains 'AnixStudyAssist'
            if "AnixStudyAssist" in filename:
                new_filename = filename.replace("AnixStudyAssist", "AnixStudyAssist")
                new_file_path = os.path.join(dirpath, new_filename)
                os.rename(file_path, new_file_path)

    print("Step 2: Moving directories and switching java -> kotlin...")
    # We do this after content replacement to ensure paths are correct
    for module in ["app", "core", "feature/auth", "feature/class-details", "feature/landing", "feature/settings"]:
        for source_set in ["main", "test", "androidTest"]:
            old_base = os.path.join(root_dir, module, "src", source_set, "java")
            if os.path.exists(old_base):
                # Target path: .../src/main/kotlin/com/anix/android/anixstudyassist
                # Note: We handle the nested samsung/android move

                old_pkg_path = os.path.join(old_base, "com", "samsung", "android")
                new_base = os.path.join(root_dir, module, "src", source_set, "kotlin")
                new_pkg_path_base = os.path.join(new_base, "com", "anix", "android")

                if os.path.exists(old_pkg_path):
                    # List all subfolders in com/samsung/android (anixstudyassist, feature, etc)
                    for item in os.listdir(old_pkg_path):
                        old_item_path = os.path.join(old_pkg_path, item)

                        target_name = item
                        if item == "anixstudyassist":
                            target_name = "anixstudyassist"

                        new_item_path = os.path.join(new_pkg_path_base, target_name)

                        os.makedirs(os.path.dirname(new_item_path), exist_ok=True)
                        shutil.move(old_item_path, new_item_path)

                    # Clean up old empty java directories
                    shutil.rmtree(old_base)
                    print(f"Moved {module} {source_set} sources to kotlin and updated package structure.")

    print("\nRefactoring complete! Please Sync Project with Gradle in Android Studio.")

if __name__ == "__main__":
    refactor_project()