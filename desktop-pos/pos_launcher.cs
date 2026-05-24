using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Windows.Forms;

class CuratorPOSLauncher {
    [STAThread]
    static void Main(string[] args) {
        // Locate the JAR relative to this EXE
        string exeDir = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
        string jarPath = Path.Combine(exeDir, "CuratorPOS.jar");

        if (!File.Exists(jarPath)) {
            MessageBox.Show(
                "CuratorPOS.jar not found!\n\nPlease ensure CuratorPOS.jar is in the same folder as CuratorPOS.exe.",
                "Curator POS - Launch Error",
                MessageBoxButtons.OK,
                MessageBoxIcon.Error
            );
            return;
        }

        // Try to find javaw.exe (no console window)
        string javaExe = FindJava();
        if (javaExe == null) {
            MessageBox.Show(
                "Java 17+ is required to run Curator POS.\n\nPlease install Java from https://adoptium.net",
                "Curator POS - Java Not Found",
                MessageBoxButtons.OK,
                MessageBoxIcon.Warning
            );
            return;
        }

        ProcessStartInfo psi = new ProcessStartInfo();
        psi.FileName = javaExe;
        // --add-opens required for JavaFX reflection on Java 17+
        psi.Arguments = string.Format(
            "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED " +
            "--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED " +
            "-jar \"{0}\"",
            jarPath
        );
        psi.WorkingDirectory = exeDir;
        psi.UseShellExecute = false;
        psi.CreateNoWindow = false;

        try {
            Process.Start(psi);
        } catch (Exception ex) {
            MessageBox.Show("Failed to launch: " + ex.Message, "Curator POS Launch Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }

    static string FindJava() {
        // 1. Check JetBrains JBR (used by IntelliJ, always available in this project)
        string jbrPath = @"C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr\bin\javaw.exe";
        if (File.Exists(jbrPath)) return jbrPath;

        // 2. Check JAVA_HOME environment variable
        string javaHome = Environment.GetEnvironmentVariable("JAVA_HOME");
        if (!string.IsNullOrEmpty(javaHome)) {
            string candidate = Path.Combine(javaHome, "bin", "javaw.exe");
            if (File.Exists(candidate)) return candidate;
        }

        // 3. Check common install locations
        string[] roots = {
            @"C:\Program Files\Eclipse Adoptium",
            @"C:\Program Files\Java",
            @"C:\Program Files\Microsoft",
            @"C:\Program Files\Amazon Corretto"
        };
        foreach (string root in roots) {
            if (!Directory.Exists(root)) continue;
            foreach (string dir in Directory.GetDirectories(root)) {
                string candidate = Path.Combine(dir, "bin", "javaw.exe");
                if (File.Exists(candidate)) return candidate;
            }
        }

        // 4. Fallback to PATH
        return "javaw.exe";
    }
}
