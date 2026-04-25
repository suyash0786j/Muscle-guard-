package com.muscleguard;

// =============================================================================
//  MuscleGuard Pro — Complete Injury Recovery & Fitness Management System
//  Eclipse IDE Ready | Java 8+ | No External Libraries Required
//  Single-file build: compile with javac MuscleGuardPro.java
// =============================================================================

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;

/**
 * MuscleGuard Pro - Main Application Entry Point
 * Manages the overall application lifecycle and module navigation.
 */
public class MuscleGuardPro extends JFrame {

    // =========================================================================
    //  CONSTANTS & THEME
    // =========================================================================
    static final Color BG_DARK       = new Color(0x1a1a2e);
    static final Color BG_CARD       = new Color(0x16213e);
    static final Color BG_CARD2      = new Color(0x0f3460);
    static final Color ACCENT        = new Color(0x533483);
    static final Color ACCENT2       = new Color(0xe94560);
    static final Color GREEN         = new Color(0x00b894);
    static final Color ORANGE        = new Color(0xfdcb6e);
    static final Color RED_COLOR     = new Color(0xe17055);
    static final Color TEXT_PRIMARY  = new Color(0xedf2f7);
    static final Color TEXT_MUTED    = new Color(0xa0aec0);
    static final Color TEXT_ACCENT   = new Color(0x81ecec);
    static final Color BORDER_COLOR  = new Color(0x2d3748);

    static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 28);
    static final Font FONT_HEADING   = new Font("Segoe UI", Font.BOLD, 18);
    static final Font FONT_SUB       = new Font("Segoe UI", Font.BOLD, 14);
    static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font FONT_MONO      = new Font("Consolas", Font.PLAIN, 12);

    static final String[] QUOTES = {
        "\"Pain is temporary. Glory is forever.\" — Arnold Schwarzenegger",
        "\"Your body can stand almost anything. It's your mind you have to convince.\"",
        "\"The pain you feel today will be the strength you feel tomorrow.\"",
        "\"Recovery is not a race. You don't have to feel guilty for taking the time you need.\"",
        "\"Healing is a matter of time, but it is sometimes also a matter of opportunity.\"",
        "\"Every day is another chance to get stronger.\"",
        "\"The comeback is always stronger than the setback.\"",
    };

    // =========================================================================
    //  APPLICATION STATE
    // =========================================================================
    static UserProfile      currentUser    = null;
    static InjuryProfile    currentInjury  = null;
    static ProgressTracker  tracker        = null;

    // =========================================================================
    //  UI COMPONENTS
    // =========================================================================
    private JPanel          mainContainer;
    private JPanel          sidebarPanel;
    private JPanel          contentArea;
    private JPanel          currentPanel;
    private CardLayout      cardLayout;
    private JPanel          cardContainer;

    // Module panels
    private DashboardPanel      dashboardPanel;
    private RegistrationPanel   registrationPanel;
    private InjuryPanel         injuryPanel;
    private WorkoutPanel        workoutPanel;
    private DietPanel           dietPanel;
    private MedicalPanel        medicalPanel;
    private ProgressPanel       progressPanel;

    private Map<String, AnimatedNavButton> navButtons = new LinkedHashMap<>();
    private String activeModule = "dashboard";

    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
    public MuscleGuardPro() {
        super("💪 MuscleGuard Pro — Injury Recovery & Fitness System");
        initApp();
        setupWindow();
        buildUI();
        loadUserData();
        setVisible(true);
    }

    private void initApp() {
        tracker = new ProgressTracker();
        // Set system look but override with custom theme
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Override UI defaults for cleaner look
        UIManager.put("ToolTip.background", BG_CARD);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.font",       FONT_BODY);
        UIManager.put("ToolTip.border",     BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private void setupWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 860);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { exitApp(); }
        });
    }

    private Image createAppIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_CARD2);
        g2.fillOval(0, 0, 32, 32);
        g2.setColor(ACCENT2);
        g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        g2.drawString("💪", 4, 24);
        g2.dispose();
        return img;
    }

    // =========================================================================
    //  UI BUILDER
    // =========================================================================
    private void buildUI() {
        mainContainer = new JPanel(new BorderLayout(0, 0));
        mainContainer.setBackground(BG_DARK);
        setContentPane(mainContainer);

        buildTitleBar();
        buildSidebar();
        buildContentArea();
    }

    private void buildTitleBar() {
        JPanel titleBar = new GradientPanel(BG_CARD2, new Color(0x533483));
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, 56));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Logo section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        logoPanel.setOpaque(false);

        JLabel logo = new JLabel("💪 MuscleGuard Pro");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(TEXT_PRIMARY);
        logoPanel.add(logo);

        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(FONT_SMALL);
        versionLabel.setForeground(TEXT_MUTED);
        logoPanel.add(versionLabel);
        titleBar.add(logoPanel, BorderLayout.WEST);

        // Right section — date/time + actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        rightPanel.setOpaque(false);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_MUTED);
        updateDateTime(dateLabel);
        Timer clockTimer = new Timer(1000, e -> updateDateTime(dateLabel));
        clockTimer.start();
        rightPanel.add(dateLabel);

        // Exit button
        AnimatedButton exitBtn = new AnimatedButton("✕", ACCENT2, new Color(0xc0392b));
        exitBtn.setPreferredSize(new Dimension(32, 28));
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exitBtn.addActionListener(e -> exitApp());
        exitBtn.setToolTipText("Exit Application");
        rightPanel.add(exitBtn);

        titleBar.add(rightPanel, BorderLayout.EAST);
        mainContainer.add(titleBar, BorderLayout.NORTH);
    }

    private void updateDateTime(JLabel lbl) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy  |  HH:mm:ss");
        lbl.setText(sdf.format(new Date()));
    }

    private void buildSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(BG_CARD);
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Nav items container
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(BG_CARD);
        navContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // User avatar card
        JPanel avatarCard = createAvatarCard();
        navContainer.add(avatarCard);
        navContainer.add(Box.createRigidArea(new Dimension(0, 16)));

        // Nav sections
        addNavSection(navContainer, "MAIN");
        addNavButton(navContainer, "dashboard",   "🏠", "Dashboard");
        addNavSection(navContainer, "MODULES");
        addNavButton(navContainer, "register",    "👤", "My Profile");
        addNavButton(navContainer, "injury",      "🩹", "Injury Setup");
        addNavButton(navContainer, "workout",     "🏋️", "Workout Planner");
        addNavButton(navContainer, "diet",        "🥗", "Diet Planner");
        addNavButton(navContainer, "medical",     "⚕", "Medical Tracker");
        addNavButton(navContainer, "progress",    "📊", "Progress");
        navContainer.add(Box.createVerticalGlue());

        // Version info at bottom
        JPanel bottomInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomInfo.setBackground(BG_CARD);
        JLabel verLbl = new JLabel("MuscleGuard Pro v1.0");
        verLbl.setFont(FONT_SMALL);
        verLbl.setForeground(TEXT_MUTED);
        bottomInfo.add(verLbl);

        JScrollPane navScroll = new JScrollPane(navContainer);
        navScroll.setBorder(null);
        navScroll.setBackground(BG_CARD);
        navScroll.getViewport().setBackground(BG_CARD);
        navScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        navScroll.getVerticalScrollBar().setUnitIncrement(16);

        sidebarPanel.add(navScroll, BorderLayout.CENTER);
        sidebarPanel.add(bottomInfo, BorderLayout.SOUTH);
        mainContainer.add(sidebarPanel, BorderLayout.WEST);
    }

    private JPanel createAvatarCard() {
        JPanel card = new RoundedPanel(12, BG_CARD2);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel avatarLbl = new JLabel("🏃", SwingConstants.CENTER);
        avatarLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        avatarLbl.setPreferredSize(new Dimension(50, 50));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel nameLbl = new JLabel(currentUser != null ? currentUser.name : "Set Up Profile");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(TEXT_PRIMARY);

        JLabel statusLbl = new JLabel(currentInjury != null ? currentInjury.type : "No Injury Set");
        statusLbl.setFont(FONT_SMALL);
        statusLbl.setForeground(TEXT_MUTED);

        infoPanel.add(nameLbl);
        infoPanel.add(statusLbl);

        card.add(avatarLbl, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }

    private void addNavSection(JPanel parent, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 8, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
    }

    private void addNavButton(JPanel parent, String module, String icon, String label) {
        AnimatedNavButton btn = new AnimatedNavButton(icon, label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addActionListener(e -> switchModule(module));
        navButtons.put(module, btn);
        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 2)));
    }

    private void buildContentArea() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_DARK);

        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(BG_DARK);

        // Build all module panels
        dashboardPanel    = new DashboardPanel(this);
        registrationPanel = new RegistrationPanel(this);
        injuryPanel       = new InjuryPanel(this);
        workoutPanel      = new WorkoutPanel(this);
        dietPanel         = new DietPanel(this);
        medicalPanel      = new MedicalPanel(this);
        progressPanel     = new ProgressPanel(this);

        cardContainer.add(dashboardPanel,    "dashboard");
        cardContainer.add(registrationPanel, "register");
        cardContainer.add(injuryPanel,       "injury");
        cardContainer.add(workoutPanel,      "workout");
        cardContainer.add(dietPanel,         "diet");
        cardContainer.add(medicalPanel,      "medical");
        cardContainer.add(progressPanel,     "progress");

        contentArea.add(cardContainer, BorderLayout.CENTER);
        mainContainer.add(contentArea, BorderLayout.CENTER);

        switchModule("dashboard");
    }

    // =========================================================================
    //  MODULE NAVIGATION
    // =========================================================================
    void switchModule(String module) {
        // Deactivate old
        AnimatedNavButton oldBtn = navButtons.get(activeModule);
        if (oldBtn != null) oldBtn.setActive(false);

        // Activate new
        activeModule = module;
        AnimatedNavButton newBtn = navButtons.get(module);
        if (newBtn != null) newBtn.setActive(true);

        cardLayout.show(cardContainer, module);

        // Refresh panels when shown
        switch (module) {
            case "dashboard": dashboardPanel.refresh();    break;
            case "workout":   workoutPanel.refresh();      break;
            case "diet":      dietPanel.refresh();         break;
            case "progress":  progressPanel.refresh();     break;
        }
    }

    void refreshSidebar() {
        sidebarPanel.removeAll();
        buildSidebar();
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    // =========================================================================
    //  DATA PERSISTENCE
    // =========================================================================
    private void loadUserData() {
        // Load user profile
        File f = new File("user_data.txt");
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String name   = readLine(br);
                int    age    = parseInt(readLine(br), 0);
                String gender = readLine(br);
                double weight = parseDouble(readLine(br), 0);
                double height = parseDouble(readLine(br), 0);
                if (name != null && !name.isEmpty()) {
                    currentUser = new UserProfile(name, age, gender, weight, height);
                }
            } catch (IOException ignored) {}
        }

        // Load injury profile
        File fi = new File("injury_data.txt");
        if (fi.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fi))) {
                String type  = readLine(br);
                int    level = parseInt(readLine(br), 5);
                String desc  = readLine(br);
                if (type != null && !type.isEmpty()) {
                    currentInjury = new InjuryProfile(type, level, desc);
                }
            } catch (IOException ignored) {}
        }

        // Load progress data
        tracker.load();
    }

    static void saveUserData() {
        if (currentUser == null) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter("user_data.txt"))) {
            pw.println(currentUser.name);
            pw.println(currentUser.age);
            pw.println(currentUser.gender);
            pw.println(currentUser.weightKg);
            pw.println(currentUser.heightCm);
        } catch (IOException ignored) {}
    }

    static void saveInjuryData() {
        if (currentInjury == null) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter("injury_data.txt"))) {
            pw.println(currentInjury.type);
            pw.println(currentInjury.painLevel);
            pw.println(currentInjury.description);
        } catch (IOException ignored) {}
    }

    private void exitApp() {
        int r = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit MuscleGuard Pro?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            if (tracker != null) tracker.save();
            System.exit(0);
        }
    }

    // Helpers
    private String readLine(BufferedReader br) {
        try { return br.readLine(); } catch (IOException e) { return null; }
    }
    static int parseInt(String s, int def) {
        try { return Integer.parseInt(s == null ? "" : s.trim()); }
        catch (NumberFormatException e) { return def; }
    }
    static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s == null ? "" : s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    // =========================================================================
    //  ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MuscleGuardPro());
    }


    // =========================================================================
    //  DATA MODELS
    // =========================================================================

    /** User profile data */
    static class UserProfile {
        String name; int age; String gender;
        double weightKg, heightCm;

        UserProfile(String name, int age, String gender, double weightKg, double heightCm) {
            this.name = name; this.age = age; this.gender = gender;
            this.weightKg = weightKg; this.heightCm = heightCm;
        }

        double getBMI() {
            double hm = heightCm / 100.0;
            return (hm > 0) ? weightKg / (hm * hm) : 0;
        }

        String getBMICategory() {
            double bmi = getBMI();
            if (bmi < 18.5) return "Underweight";
            if (bmi < 25.0) return "Normal";
            if (bmi < 30.0) return "Overweight";
            return "Obese";
        }

        /** Harris-Benedict BMR */
        double getBMR() {
            if ("Male".equalsIgnoreCase(gender))
                return 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
            return 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        }

        double getTDEE() { return getBMR() * 1.4; } // moderate activity
    }

    /** Injury profile data */
    static class InjuryProfile {
        String type; int painLevel; String description;

        InjuryProfile(String type, int painLevel, String description) {
            this.type = type; this.painLevel = painLevel;
            this.description = (description != null) ? description : "";
        }

        int getRecoveryDays() {
            int base;
            switch (type) {
                case "Back Pain":     base = 14; break;
                case "Neck Pain":     base = 7;  break;
                case "Knee Injury":   base = 30; break;
                case "Shoulder Pain": base = 21; break;
                case "Elbow Pain":    base = 14; break;
                case "Wrist Pain":    base = 10; break;
                case "Ankle Sprain":  base = 21; break;
                default:              base = 14;
            }
            return base + (painLevel * 2);
        }
    }

    /** Single exercise definition */
    static class Exercise {
        String name, emoji, instructions, difficulty;
        int durationSec;
        boolean completed;

        Exercise(String name, String emoji, String instructions, String difficulty, int durationSec) {
            this.name = name; this.emoji = emoji; this.instructions = instructions;
            this.difficulty = difficulty; this.durationSec = durationSec;
            this.completed = false;
        }
    }

    /** Medication reminder */
    static class Medication {
        String name, time, notes;
        boolean takenToday;

        Medication(String name, String time, String notes) {
            this.name = name; this.time = time; this.notes = notes;
            this.takenToday = false;
        }
    }

    /** Diary entry */
    static class DiaryEntry {
        String date, symptoms, mood;
        int painLevel;

        DiaryEntry(String date, int painLevel, String symptoms, String mood) {
            this.date = date; this.painLevel = painLevel;
            this.symptoms = symptoms; this.mood = mood;
        }
    }

    // =========================================================================
    //  PROGRESS TRACKER
    // =========================================================================
    static class ProgressTracker {
        List<DiaryEntry>  diaryEntries   = new ArrayList<>();
        List<Medication>  medications    = new ArrayList<>();
        List<String>      completedDates = new ArrayList<>();
        int               streakDays     = 0;
        int               totalExercises = 0;

        void addDiaryEntry(DiaryEntry e) { diaryEntries.add(e); save(); }

        double getRecoveryPercent() {
            if (currentInjury == null) return 0;
            int totalDays = currentInjury.getRecoveryDays();
            int daysDone  = completedDates.size();
            return Math.min(100.0, (daysDone * 100.0) / totalDays);
        }

        void save() {
            // Save diary
            try (PrintWriter pw = new PrintWriter(new FileWriter("progress.txt"))) {
                for (DiaryEntry e : diaryEntries)
                    pw.printf("%s|%d|%s|%s%n", e.date, e.painLevel, e.symptoms, e.mood);
            } catch (IOException ignored) {}

            // Save medications
            try (PrintWriter pw = new PrintWriter(new FileWriter("medications.txt"))) {
                for (Medication m : medications)
                    pw.printf("%s|%s|%s%n", m.name, m.time, m.notes);
            } catch (IOException ignored) {}
        }

        void load() {
            // Load diary
            File f = new File("progress.txt");
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split("\\|", 4);
                        if (p.length == 4)
                            diaryEntries.add(new DiaryEntry(p[0], parseInt(p[1], 5), p[2], p[3]));
                    }
                } catch (IOException ignored) {}
            }

            // Load medications
            File fm = new File("medications.txt");
            if (fm.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(fm))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split("\\|", 3);
                        if (p.length >= 2)
                            medications.add(new Medication(p[0], p[1], p.length > 2 ? p[2] : ""));
                    }
                } catch (IOException ignored) {}
            }

            // Calculate streak
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (!completedDates.contains(today)) {
                completedDates.add(today);
            }
            streakDays = completedDates.size();
        }
    }


    // =========================================================================
    //  EXERCISE LIBRARY
    // =========================================================================
    static class ExerciseLibrary {
        static Map<String, List<Exercise>> library = new HashMap<>();

        static {
            library.put("Back Pain", new ArrayList<>(Arrays.asList(
                new Exercise("Cat-Cow Stretch",
                    "🐱", "Start on hands and knees. Alternate between arching back up (Cat) and letting it sag down (Cow). Breathe deeply.",
                    "Beginner", 30),
                new Exercise("Child's Pose",
                    "🧘", "Kneel and sit back on heels. Reach arms forward on floor, hold comfortably. Great for lower back.",
                    "Beginner", 30),
                new Exercise("Pelvic Tilt",
                    "↕️", "Lie on back, knees bent. Flatten lower back against floor by tightening abs, hold 5s. Release.",
                    "Beginner", 30),
                new Exercise("Knee-to-Chest",
                    "🦵", "Lie on back, pull one knee to chest, hold 5s. Alternate legs. Gently relieves lower back tension.",
                    "Beginner", 30),
                new Exercise("Bridge Exercise",
                    "🌉", "Lie on back, knees bent. Lift hips off floor until body forms straight line. Hold 5s, lower slowly.",
                    "Intermediate", 30)
            )));

            library.put("Neck Pain", new ArrayList<>(Arrays.asList(
                new Exercise("Neck Rotations",
                    "🔄", "Slowly turn head left until chin is over shoulder. Hold 5s. Return to center. Repeat on right side.",
                    "Beginner", 30),
                new Exercise("Side Bends",
                    "↔️", "Tilt head sideways, ear toward shoulder. Hold 5 seconds each side. Do not raise shoulder.",
                    "Beginner", 30),
                new Exercise("Chin Tucks",
                    "👇", "Stand against wall. Gently tuck chin toward chest, making a 'double chin'. Hold 5s. Strengthens deep neck flexors.",
                    "Beginner", 30),
                new Exercise("Shoulder Rolls",
                    "💫", "Roll shoulders backward in large circles, 5 times. Then forward 5 times. Relieves neck and upper back tension.",
                    "Beginner", 30)
            )));

            library.put("Knee Injury", new ArrayList<>(Arrays.asList(
                new Exercise("Quad Sets",
                    "💪", "Sit with leg straight. Tighten thigh muscle, pushing knee down. Hold 5-10s. Builds quad strength safely.",
                    "Beginner", 30),
                new Exercise("Straight Leg Raises",
                    "🦵", "Lie on back. Keep one leg straight, tighten quad, lift leg 12 inches. Lower slowly. Repeat 10x each leg.",
                    "Beginner", 30),
                new Exercise("Hamstring Curls",
                    "🔄", "Stand holding chair for balance. Slowly bend knee, lifting foot toward buttocks. Hold 5s. Lower slowly.",
                    "Intermediate", 30),
                new Exercise("Ankle Pumps",
                    "⬆️", "Sit or lie down. Flex and point foot repeatedly. Improves circulation and reduces swelling in knee.",
                    "Beginner", 30),
                new Exercise("Mini Squats",
                    "🏋️", "Stand holding chair. Bend knees slightly (30°). Hold 5s. Strengthens without stressing the joint.",
                    "Intermediate", 30)
            )));

            library.put("Shoulder Pain", new ArrayList<>(Arrays.asList(
                new Exercise("Pendulum Swings",
                    "⏰", "Lean forward, arm hanging. Gently swing arm in small circles. Gravity traction reduces shoulder pressure.",
                    "Beginner", 30),
                new Exercise("Cross-Body Stretch",
                    "✕", "Bring one arm across chest. Hold with other arm at elbow. Stretches posterior shoulder capsule.",
                    "Beginner", 30),
                new Exercise("Doorway Stretch",
                    "🚪", "Place forearms on door frame. Step through gently to stretch chest and front shoulder. Hold 20-30s.",
                    "Beginner", 30),
                new Exercise("Wall Slides",
                    "⬆️", "Stand with back to wall. Slide arms up and down wall in W and Y positions. Improves scapular mobility.",
                    "Intermediate", 30)
            )));

            library.put("Elbow Pain", new ArrayList<>(Arrays.asList(
                new Exercise("Wrist Extension Stretch",
                    "✋", "Extend arm, palm down. Use other hand to gently bend wrist downward. Hold 15-30s. Stretches forearm extensors.",
                    "Beginner", 30),
                new Exercise("Wrist Flexion Stretch",
                    "🤚", "Extend arm palm up. Gently bend wrist toward floor. Hold 15-30s. Stretches forearm flexors.",
                    "Beginner", 30),
                new Exercise("Elbow Flexion",
                    "💪", "Bend elbow fully, bringing hand to shoulder. Straighten slowly. Use minimal resistance initially.",
                    "Beginner", 30),
                new Exercise("Pronation & Supination",
                    "🔄", "Elbow at side, bent 90°. Rotate forearm palm up then palm down. Improves forearm rotation.",
                    "Beginner", 30)
            )));

            library.put("Wrist Pain", new ArrayList<>(Arrays.asList(
                new Exercise("Wrist Circles",
                    "🔄", "Make gentle circles with wrists, clockwise and counter-clockwise. 10 each direction.",
                    "Beginner", 30),
                new Exercise("Finger Spread",
                    "✋", "Spread fingers as wide as possible, hold 5s. Then make a fist. Improves finger flexors/extensors.",
                    "Beginner", 30),
                new Exercise("Prayer Stretch",
                    "🙏", "Press palms together in prayer position, lower hands until stretch felt in wrists. Hold 15-30s.",
                    "Beginner", 30),
                new Exercise("Grip Strengthening",
                    "🤜", "Squeeze a soft ball or rolled towel firmly for 5 seconds. Release. Rebuilds grip strength safely.",
                    "Beginner", 30)
            )));

            library.put("Ankle Sprain", new ArrayList<>(Arrays.asList(
                new Exercise("Ankle Pumps",
                    "⬆️", "Sit or lie. Flex foot (toes toward shin), then point (toes away). 20 reps. Reduces swelling.",
                    "Beginner", 30),
                new Exercise("Ankle Alphabet",
                    "🔤", "Draw alphabet with foot in air. This exercises all planes of ankle motion gently.",
                    "Beginner", 30),
                new Exercise("Towel Scrunches",
                    "👣", "Place towel on floor. Scrunch towel toward you with toes. Strengthens foot intrinsic muscles.",
                    "Beginner", 30),
                new Exercise("Calf Raises",
                    "⬆️", "Stand holding chair. Rise onto tiptoes slowly, lower slowly. Strengthens calf and stabilizes ankle.",
                    "Intermediate", 30),
                new Exercise("Single Leg Balance",
                    "🧘", "Stand on affected leg for 20-30 seconds. Progress to wobble board. Restores proprioception.",
                    "Intermediate", 30)
            )));
        }

        static List<Exercise> getExercises(String injuryType) {
            return library.getOrDefault(injuryType, new ArrayList<>());
        }
    }


    // =========================================================================
    //  CUSTOM UI COMPONENTS
    // =========================================================================

    /** Gradient background panel */
    static class GradientPanel extends JPanel {
        private Color c1, c2;
        GradientPanel(Color c1, Color c2) {
            this.c1 = c1; this.c2 = c2;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    /** Rounded-corner panel */
    static class RoundedPanel extends JPanel {
        private int radius; private Color bg;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius; this.bg = bg;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Animated button with hover effect */
    static class AnimatedButton extends JButton {
        private Color normalColor, hoverColor, currentBg;
        private float hoverProgress = 0f;
        private Timer hoverTimer;

        AnimatedButton(String text, Color normal, Color hover) {
            super(text);
            this.normalColor = normal; this.hoverColor = hover;
            this.currentBg = normal;
            setOpaque(false); setContentAreaFilled(false);
            setBorderPainted(false); setFocusPainted(false);
            setForeground(TEXT_PRIMARY);
            setFont(FONT_BODY);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            hoverTimer = new Timer(16, e -> {
                currentBg = blendColor(normalColor, hoverColor, hoverProgress);
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hoverTimer.stop();
                    hoverProgress = 1f; currentBg = hoverColor; repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hoverTimer.stop();
                    hoverProgress = 0f; currentBg = normalColor; repaint();
                }
                @Override public void mousePressed(MouseEvent e)  { currentBg = hoverColor.darker(); repaint(); }
                @Override public void mouseReleased(MouseEvent e) { currentBg = hoverColor; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }

        static Color blendColor(Color c1, Color c2, float t) {
            t = Math.max(0, Math.min(1, t));
            int r = (int)(c1.getRed()   + t*(c2.getRed()   - c1.getRed()));
            int g = (int)(c1.getGreen() + t*(c2.getGreen() - c1.getGreen()));
            int b = (int)(c1.getBlue()  + t*(c2.getBlue()  - c1.getBlue()));
            return new Color(r, g, b);
        }
    }

    /** Sidebar navigation button with active state */
    static class AnimatedNavButton extends JPanel {
        private String icon, label;
        private boolean active = false;
        private boolean hovered = false;
        private List<ActionListener> listeners = new ArrayList<>();

        AnimatedNavButton(String icon, String label) {
            this.icon = icon; this.label = label;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) {
                    ActionEvent ae = new ActionEvent(AnimatedNavButton.this,
                        ActionEvent.ACTION_PERFORMED, label);
                    for (ActionListener l : listeners) l.actionPerformed(ae);
                }
            });
        }

        void addActionListener(ActionListener l) { listeners.add(l); }
        void setActive(boolean a) { active = a; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setPaint(new GradientPaint(0, 0, BG_CARD2, getWidth(), 0, ACCENT));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // Accent bar on left
                g2.setColor(ACCENT2);
                g2.fillRoundRect(0, 4, 3, getHeight()-8, 3, 3);
            } else if (hovered) {
                g2.setColor(new Color(0x0f3460, false));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }

            // Icon
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            g2.setColor(active ? TEXT_PRIMARY : TEXT_MUTED);
            g2.drawString(icon, 14, getHeight() - (getHeight() - 16) / 2);

            // Label
            g2.setFont(active ? new Font("Segoe UI", Font.BOLD, 13) : FONT_BODY);
            g2.setColor(active ? TEXT_PRIMARY : TEXT_MUTED);
            g2.drawString(label, 38, getHeight() - (getHeight() - 13) / 2);

            g2.dispose();
        }

        @Override public Dimension getPreferredSize() { return new Dimension(200, 44); }
    }

    /** Card panel with title */
    static JPanel makeCard(String title, String emoji) {
        JPanel card = new RoundedPanel(12, BG_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        if (title != null && !title.isEmpty()) {
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
            headerPanel.setBackground(BG_CARD2);
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

            JLabel headerLbl = new JLabel(emoji + "  " + title);
            headerLbl.setFont(FONT_SUB);
            headerLbl.setForeground(TEXT_PRIMARY);
            headerPanel.add(headerLbl);
            card.add(headerPanel, BorderLayout.NORTH);
        }
        return card;
    }

    /** Styled text field */
    static JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(BG_CARD2);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(200, 36));
        return tf;
    }

    /** Styled combo box */
    static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG_CARD2);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        cb.setPreferredSize(new Dimension(200, 36));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, value, idx, sel, foc);
                setBackground(sel ? BG_CARD2 : BG_DARK);
                setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return cb;
    }

    /** Create a section label */
    static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_ACCENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    /** Styled area label */
    static JTextArea makeTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setBackground(BG_CARD2);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_ACCENT);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return ta;
    }

    /** Scroll pane with dark theme */
    static JScrollPane darkScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
        return sp;
    }

    // =========================================================================
    //  CIRCULAR PROGRESS COMPONENT
    // =========================================================================
    static class CircularProgress extends JPanel {
        private int value = 0, maxValue = 100;
        private String centerText = "";
        private Color progressColor = GREEN;
        private float animValue = 0f;
        private Timer animTimer;

        CircularProgress(int size) {
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }

        void setValue(int v) {
            this.value = v;
            this.centerText = v + "%";
            // Animate to new value
            final float target = v / (float)maxValue;
            final float current = animValue;
            if (animTimer != null) animTimer.stop();
            animTimer = new Timer(16, null);
            animTimer.addActionListener(e -> {
                animValue += (target - animValue) * 0.15f;
                if (Math.abs(animValue - target) < 0.005f) {
                    animValue = target; ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            animTimer.start();
        }

        void setProgressColor(Color c) { this.progressColor = c; }
        void setCenterText(String t)   { this.centerText = t; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - 8;
            int x = (w - size) / 2, y = (h - size) / 2;
            int stroke = Math.max(8, size / 8);

            // Background arc
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(BORDER_COLOR);
            g2.drawOval(x + stroke/2, y + stroke/2, size - stroke, size - stroke);

            // Progress arc
            g2.setColor(progressColor);
            g2.drawArc(x + stroke/2, y + stroke/2, size - stroke, size - stroke,
                90, (int)(-360 * animValue));

            // Center text
            g2.setFont(new Font("Segoe UI", Font.BOLD, size / 6));
            g2.setColor(TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(centerText)) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(centerText, tx, ty);
            g2.dispose();
        }
    }

    // =========================================================================
    //  LINE CHART COMPONENT
    // =========================================================================
    static class LineChart extends JPanel {
        private List<Integer> values   = new ArrayList<>();
        private List<String>  labels   = new ArrayList<>();
        private String title = "Chart";
        private Color lineColor = TEXT_ACCENT;

        LineChart(String title) {
            this.title = title;
            setBackground(BG_CARD2);
            setPreferredSize(new Dimension(400, 200));
        }

        void setData(List<Integer> vals, List<String> lbls) {
            this.values = new ArrayList<>(vals);
            this.labels = new ArrayList<>(lbls);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pad = 40, chartW = w - pad*2, chartH = h - pad*2;

            // Background
            g2.setColor(BG_CARD2);
            g2.fillRect(0, 0, w, h);

            // Title
            g2.setFont(FONT_SUB);
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(title, pad, 20);

            if (values.isEmpty()) {
                g2.setFont(FONT_BODY);
                g2.setColor(TEXT_MUTED);
                g2.drawString("No data yet", w/2 - 40, h/2);
                g2.dispose(); return;
            }

            // Grid lines
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10, new float[]{3, 4}, 0));
            for (int i = 0; i <= 5; i++) {
                int y = pad + chartH - (i * chartH / 5);
                g2.drawLine(pad, y, pad + chartW, y);
                g2.setColor(TEXT_MUTED);
                g2.setFont(FONT_SMALL);
                g2.drawString(String.valueOf(i * 2), pad - 20, y + 4);
                g2.setColor(BORDER_COLOR);
            }

            // Data points and lines
            if (values.size() > 1) {
                int maxVal = 10;
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(lineColor);

                int[] xs = new int[values.size()];
                int[] ys = new int[values.size()];
                int step = chartW / Math.max(1, values.size() - 1);

                for (int i = 0; i < values.size(); i++) {
                    xs[i] = pad + i * step;
                    ys[i] = pad + chartH - (int)((values.get(i) / (double)maxVal) * chartH);
                }

                // Area fill
                int[] polyX = new int[values.size() + 2];
                int[] polyY = new int[values.size() + 2];
                System.arraycopy(xs, 0, polyX, 0, xs.length);
                System.arraycopy(ys, 0, polyY, 0, ys.length);
                polyX[values.size()] = xs[xs.length-1];
                polyY[values.size()] = pad + chartH;
                polyX[values.size()+1] = xs[0];
                polyY[values.size()+1] = pad + chartH;
                g2.setColor(new Color(lineColor.getRed(), lineColor.getGreen(),
                    lineColor.getBlue(), 50));
                g2.fillPolygon(polyX, polyY, polyX.length);

                // Lines
                g2.setColor(lineColor);
                for (int i = 1; i < values.size(); i++)
                    g2.drawLine(xs[i-1], ys[i-1], xs[i], ys[i]);

                // Dots
                for (int i = 0; i < values.size(); i++) {
                    g2.setColor(BG_CARD);
                    g2.fillOval(xs[i]-5, ys[i]-5, 10, 10);
                    g2.setColor(lineColor);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(xs[i]-5, ys[i]-5, 10, 10);
                }

                // X labels
                g2.setFont(FONT_SMALL);
                g2.setColor(TEXT_MUTED);
                for (int i = 0; i < labels.size() && i < xs.length; i++) {
                    String lbl = labels.get(i);
                    int lx = xs[i] - g2.getFontMetrics().stringWidth(lbl)/2;
                    g2.drawString(lbl, lx, h - 8);
                }
            }
            g2.dispose();
        }
    }


    // =========================================================================
    //  MODULE 1: DASHBOARD
    // =========================================================================
    static class DashboardPanel extends JPanel {
        private MuscleGuardPro app;
        private JLabel welcomeLbl, quoteLbl, recoveryLbl;
        private CircularProgress recoveryCircle;
        private JPanel statsRow, quickActionsPanel;

        DashboardPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            // Scrollable container
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Header gradient
            JPanel header = new GradientPanel(BG_CARD2, new Color(0x533483));
            header.setLayout(new BorderLayout());
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            header.setPreferredSize(new Dimension(100, 120));
            header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            welcomeLbl = new JLabel("👋 Welcome back!");
            welcomeLbl.setFont(FONT_TITLE);
            welcomeLbl.setForeground(TEXT_PRIMARY);

            JLabel subLbl = new JLabel("Your recovery journey continues today.");
            subLbl.setFont(FONT_BODY);
            subLbl.setForeground(TEXT_MUTED);

            JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 6));
            headerText.setOpaque(false);
            headerText.add(welcomeLbl);
            headerText.add(subLbl);
            header.add(headerText, BorderLayout.CENTER);

            JLabel dayLbl = new JLabel("Day " + Math.max(1, tracker.streakDays));
            dayLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
            dayLbl.setForeground(ORANGE);
            header.add(dayLbl, BorderLayout.EAST);

            content.add(header);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Stats row
            statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
            statsRow.setBackground(BG_DARK);
            statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            buildStatsRow();
            content.add(statsRow);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Main content: Recovery + Today's Workout
            JPanel mainRow = new JPanel(new GridLayout(1, 2, 14, 0));
            mainRow.setBackground(BG_DARK);
            mainRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

            // Recovery progress card
            JPanel recCard = makeCard("Recovery Progress", "🔄");
            JPanel recContent = new JPanel(new BorderLayout());
            recContent.setBackground(BG_CARD);
            recContent.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            recoveryCircle = new CircularProgress(150);
            recoveryCircle.setProgressColor(GREEN);
            int pct = (int) tracker.getRecoveryPercent();
            recoveryCircle.setValue(pct);

            JPanel circleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            circleWrapper.setBackground(BG_CARD);
            circleWrapper.add(recoveryCircle);

            recoveryLbl = new JLabel(getRecoveryMessage(), SwingConstants.CENTER);
            recoveryLbl.setFont(FONT_BODY);
            recoveryLbl.setForeground(TEXT_MUTED);
            recoveryLbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            recContent.add(circleWrapper, BorderLayout.CENTER);
            recContent.add(recoveryLbl, BorderLayout.SOUTH);
            recCard.add(recContent, BorderLayout.CENTER);
            mainRow.add(recCard);

            // Today's workout card
            JPanel workCard = makeCard("Today's Workout", "🏋️");
            JPanel workContent = new JPanel();
            workContent.setLayout(new BoxLayout(workContent, BoxLayout.Y_AXIS));
            workContent.setBackground(BG_CARD);
            workContent.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            if (currentInjury != null) {
                List<Exercise> exs = ExerciseLibrary.getExercises(currentInjury.type);
                for (Exercise ex : exs) {
                    JPanel exRow = new JPanel(new BorderLayout());
                    exRow.setBackground(BG_CARD);
                    exRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                    exRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

                    JLabel nameL = new JLabel(ex.emoji + "  " + ex.name);
                    nameL.setFont(FONT_BODY);
                    nameL.setForeground(ex.completed ? TEXT_MUTED : TEXT_PRIMARY);
                    if (ex.completed) {
                        nameL.setText("<html><strike>" + ex.emoji + "  " + ex.name + "</strike></html>");
                    }

                    JLabel doneL = new JLabel(ex.completed ? "✅" : "○");
                    doneL.setFont(FONT_BODY);
                    doneL.setForeground(ex.completed ? GREEN : TEXT_MUTED);

                    exRow.add(nameL, BorderLayout.CENTER);
                    exRow.add(doneL, BorderLayout.EAST);
                    workContent.add(exRow);
                }
                AnimatedButton startBtn = new AnimatedButton("▶  Start Workout", BG_CARD2, ACCENT);
                startBtn.setFont(FONT_BODY);
                startBtn.setForeground(TEXT_PRIMARY);
                startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                startBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
                startBtn.addActionListener(e -> app.switchModule("workout"));
                workContent.add(Box.createRigidArea(new Dimension(0, 8)));
                workContent.add(startBtn);
            } else {
                JLabel noInjLbl = new JLabel("<html><center>⚠️ Set up your injury profile first!<br>Go to Injury Setup to get started.</center></html>");
                noInjLbl.setFont(FONT_BODY);
                noInjLbl.setForeground(ORANGE);
                noInjLbl.setHorizontalAlignment(SwingConstants.CENTER);
                workContent.add(noInjLbl);
            }
            workCard.add(workContent, BorderLayout.CENTER);
            mainRow.add(workCard);

            content.add(mainRow);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Quick actions
            quickActionsPanel = new JPanel(new GridLayout(1, 5, 12, 0));
            quickActionsPanel.setBackground(BG_DARK);
            quickActionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            buildQuickActions();
            content.add(quickActionsPanel);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Motivational quote
            JPanel quoteCard = makeCard("Daily Motivation", "💬");
            JPanel quoteContent = new JPanel(new BorderLayout());
            quoteContent.setBackground(BG_CARD);
            quoteContent.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            String quote = QUOTES[(int)(Math.abs(new Date().getTime() / 86400000L) % QUOTES.length)];
            quoteLbl = new JLabel("<html><i>" + quote + "</i></html>");
            quoteLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            quoteLbl.setForeground(TEXT_ACCENT);
            quoteContent.add(quoteLbl);
            quoteCard.add(quoteContent, BorderLayout.CENTER);
            quoteCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            content.add(quoteCard);

            add(darkScroll(content), BorderLayout.CENTER);
        }

        private void buildStatsRow() {
            statsRow.removeAll();
            String[][] stats = {
                {"🔥", String.valueOf(tracker.streakDays), "Day Streak"},
                {"💪", currentInjury != null ? currentInjury.type : "Not Set", "Injury Type"},
                {"📊", String.format("%.0f%%", tracker.getRecoveryPercent()), "Recovery"},
                {"🎯", String.valueOf(tracker.totalExercises), "Exercises Done"}
            };
            for (String[] s : stats) {
                JPanel card = new RoundedPanel(12, BG_CARD);
                card.setLayout(new BorderLayout());
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)
                ));
                JLabel iconL = new JLabel(s[0], SwingConstants.CENTER);
                iconL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                JLabel valL = new JLabel(s[1], SwingConstants.CENTER);
                valL.setFont(new Font("Segoe UI", Font.BOLD, 16));
                valL.setForeground(TEXT_ACCENT);
                JLabel lblL = new JLabel(s[2], SwingConstants.CENTER);
                lblL.setFont(FONT_SMALL);
                lblL.setForeground(TEXT_MUTED);
                JPanel center = new JPanel(new GridLayout(3, 1, 0, 2));
                center.setOpaque(false);
                center.add(iconL); center.add(valL); center.add(lblL);
                card.add(center);
                statsRow.add(card);
            }
        }

        private void buildQuickActions() {
            quickActionsPanel.removeAll();
            String[][] actions = {
                {"👤", "Profile",  "register"},
                {"🩹", "Injury",   "injury"},
                {"🏋️", "Workout", "workout"},
                {"🥗", "Diet",     "diet"},
                {"⚕",  "Medical", "medical"},
            };
            for (String[] a : actions) {
                AnimatedButton btn = new AnimatedButton(a[0] + "  " + a[1], BG_CARD, BG_CARD2);
                btn.setFont(FONT_BODY);
                btn.setForeground(TEXT_PRIMARY);
                String mod = a[2];
                btn.addActionListener(e -> app.switchModule(mod));
                btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
                quickActionsPanel.add(btn);
            }
        }

        private String getRecoveryMessage() {
            int pct = (int) tracker.getRecoveryPercent();
            if (pct == 0)  return "Your journey begins!";
            if (pct < 25)  return "Early recovery phase";
            if (pct < 50)  return "Getting stronger!";
            if (pct < 75)  return "Over halfway there!";
            if (pct < 100) return "Almost recovered!";
            return "🎉 Fully recovered!";
        }

        void refresh() {
            if (currentUser != null)
                welcomeLbl.setText("👋 Welcome, " + currentUser.name + "!");
            int pct = (int) tracker.getRecoveryPercent();
            recoveryCircle.setValue(pct);
            recoveryLbl.setText(getRecoveryMessage());
            buildStatsRow();
            statsRow.revalidate(); statsRow.repaint();
        }
    }


    // =========================================================================
    //  MODULE 2: USER REGISTRATION
    // =========================================================================
    static class RegistrationPanel extends JPanel {
        private MuscleGuardPro app;
        private JTextField nameF, ageF, weightF, heightF;
        private JComboBox<String> genderBox;
        private JLabel bmiLbl, bmiCatLbl, bmrLbl;

        RegistrationPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Header
            JLabel title = new JLabel("👤  User Profile Setup");
            title.setFont(FONT_TITLE);
            title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(title);
            content.add(Box.createRigidArea(new Dimension(0, 6)));
            JLabel sub = new JLabel("Enter your personal information for personalized recommendations");
            sub.setFont(FONT_BODY);
            sub.setForeground(TEXT_MUTED);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(sub);
            content.add(Box.createRigidArea(new Dimension(0, 20)));

            JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
            twoCol.setBackground(BG_DARK);
            twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
            twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Form card
            JPanel formCard = makeCard("Personal Information", "📋");
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(BG_CARD);
            form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 4, 6, 4);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1.0;

            nameF   = makeTextField("Your full name");
            ageF    = makeTextField("Age in years");
            weightF = makeTextField("Weight in kilograms");
            heightF = makeTextField("Height in centimeters");
            genderBox = makeCombo(new String[]{"Male", "Female", "Other"});

            String[][] rows = {
                {"Full Name", ""}, {"Age", ""}, {"Gender", ""},
                {"Weight (kg)", ""}, {"Height (cm)", ""}
            };
            Component[] inputs = {nameF, ageF, genderBox, weightF, heightF};
            for (int i = 0; i < rows.length; i++) {
                gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
                JLabel lbl = new JLabel(rows[i][0] + ":");
                lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
                form.add(lbl, gc);
                gc.gridx = 1; gc.weightx = 0.7;
                form.add(inputs[i], gc);
            }

            AnimatedButton saveBtn = new AnimatedButton("💾  Save Profile", GREEN, GREEN.darker());
            saveBtn.setFont(FONT_SUB);
            saveBtn.setForeground(BG_DARK);
            saveBtn.setPreferredSize(new Dimension(200, 42));
            saveBtn.addActionListener(e -> saveProfile());
            gc.gridx = 0; gc.gridy = rows.length; gc.gridwidth = 2;
            gc.insets = new Insets(16, 4, 4, 4);
            form.add(saveBtn, gc);

            formCard.add(form, BorderLayout.CENTER);
            twoCol.add(formCard);

            // Stats card
            JPanel statsCard = makeCard("Calculated Health Stats", "📊");
            JPanel statsContent = new JPanel();
            statsContent.setLayout(new BoxLayout(statsContent, BoxLayout.Y_AXIS));
            statsContent.setBackground(BG_CARD);
            statsContent.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            addStatRow(statsContent, "BMI Value",        "—");
            bmiLbl    = getLastLabel(statsContent);
            addStatRow(statsContent, "BMI Category",     "—");
            bmiCatLbl = getLastLabel(statsContent);
            addStatRow(statsContent, "BMR (Basal Metabolic Rate)", "—");
            bmrLbl    = getLastLabel(statsContent);
            addStatRow(statsContent, "Daily Calorie Needs (TDEE)", "—");

            statsContent.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel bmiGuide = new JLabel("<html><b>BMI Categories:</b><br>" +
                "<font color='#81ecec'>Below 18.5</font> — Underweight<br>" +
                "<font color='#00b894'>18.5–24.9</font> — Normal weight<br>" +
                "<font color='#fdcb6e'>25.0–29.9</font> — Overweight<br>" +
                "<font color='#e17055'>30.0 and above</font> — Obese</html>");
            bmiGuide.setFont(FONT_BODY);
            bmiGuide.setForeground(TEXT_PRIMARY);
            bmiGuide.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
            ));
            statsContent.add(bmiGuide);

            statsCard.add(statsContent, BorderLayout.CENTER);
            twoCol.add(statsCard);

            content.add(twoCol);

            // Load existing data
            if (currentUser != null) loadForm();

            add(darkScroll(content), BorderLayout.CENTER);
        }

        private JLabel lastLabel;
        private void addStatRow(JPanel parent, String label, String value) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(BG_CARD2);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 0, 10, 0)
            ));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            JLabel lbl = new JLabel(label);
            lbl.setFont(FONT_BODY);
            lbl.setForeground(TEXT_MUTED);
            lastLabel = new JLabel(value);
            lastLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lastLabel.setForeground(TEXT_ACCENT);
            row.add(lbl, BorderLayout.WEST);
            row.add(lastLabel, BorderLayout.EAST);
            parent.add(row);
        }

        private JLabel getLastLabel(JPanel p) { return lastLabel; }

        private void loadForm() {
            nameF.setText(currentUser.name);
            ageF.setText(String.valueOf(currentUser.age));
            weightF.setText(String.valueOf(currentUser.weightKg));
            heightF.setText(String.valueOf(currentUser.heightCm));
            genderBox.setSelectedItem(currentUser.gender);
            updateStats();
        }

        private void updateStats() {
            if (currentUser == null) return;
            bmiLbl.setText(String.format("%.1f", currentUser.getBMI()));
            bmiCatLbl.setText(currentUser.getBMICategory());
            bmrLbl.setText(String.format("%.0f kcal/day", currentUser.getBMR()));
        }

        private void saveProfile() {
            String name = nameF.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name!", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int age    = parseInt(ageF.getText(), 0);
            double w   = parseDouble(weightF.getText(), 0);
            double h   = parseDouble(heightF.getText(), 0);
            String g   = (String) genderBox.getSelectedItem();
            if (age <= 0 || w <= 0 || h <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill all numeric fields with valid values!", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentUser = new UserProfile(name, age, g, w, h);
            saveUserData();
            updateStats();
            app.refreshSidebar();
            JOptionPane.showMessageDialog(this, "✅ Profile saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // =========================================================================
    //  MODULE 3: INJURY SETUP
    // =========================================================================
    static class InjuryPanel extends JPanel {
        private MuscleGuardPro app;
        private JComboBox<String> injuryBox;
        private JSlider painSlider;
        private JLabel painLbl, recovTimeLbl;
        private JTextArea descArea;
        private JPanel painColorBar;

        InjuryPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title
            JLabel title = new JLabel("🩹  Injury Configuration");
            title.setFont(FONT_TITLE); title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(title);
            content.add(Box.createRigidArea(new Dimension(0, 6)));
            JLabel sub = new JLabel("Configure your injury for personalized recovery planning");
            sub.setFont(FONT_BODY); sub.setForeground(TEXT_MUTED);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(sub);
            content.add(Box.createRigidArea(new Dimension(0, 20)));

            JPanel twoCol = new JPanel(new GridLayout(1, 2, 16, 0));
            twoCol.setBackground(BG_DARK);
            twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
            twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Left: Injury form
            JPanel formCard = makeCard("Injury Details", "📋");
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(BG_CARD);
            form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 4, 8, 4);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1.0;

            // Injury type
            gc.gridx=0; gc.gridy=0; gc.weightx=0.3;
            JLabel injLbl = new JLabel("Injury Type:");
            injLbl.setFont(FONT_BODY); injLbl.setForeground(TEXT_MUTED);
            form.add(injLbl, gc);
            gc.gridx=1; gc.weightx=0.7;
            injuryBox = makeCombo(new String[]{
                "Back Pain", "Neck Pain", "Knee Injury",
                "Shoulder Pain", "Elbow Pain", "Wrist Pain", "Ankle Sprain"
            });
            injuryBox.addActionListener(e -> updateRecoveryTime());
            form.add(injuryBox, gc);

            // Pain level
            gc.gridx=0; gc.gridy=1; gc.weightx=0.3;
            JLabel painLabel = new JLabel("Pain Level (1-10):");
            painLabel.setFont(FONT_BODY); painLabel.setForeground(TEXT_MUTED);
            form.add(painLabel, gc);

            gc.gridx=1; gc.gridy=1; gc.gridwidth=1; gc.weightx=0.7;
            JPanel painPanel = new JPanel(new BorderLayout(8, 0));
            painPanel.setBackground(BG_CARD);

            painSlider = new JSlider(1, 10, 5);
            painSlider.setBackground(BG_CARD);
            painSlider.setForeground(TEXT_PRIMARY);
            painSlider.setMajorTickSpacing(1);
            painSlider.setPaintTicks(true);
            painSlider.setPaintLabels(true);
            // Recolor labels
            for (Component c : painSlider.getComponents()) {
                if (c instanceof JLabel) ((JLabel)c).setForeground(TEXT_MUTED);
            }

            painLbl = new JLabel("5");
            painLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            painLbl.setForeground(ORANGE);
            painLbl.setPreferredSize(new Dimension(30, 30));
            painLbl.setHorizontalAlignment(SwingConstants.CENTER);

            painSlider.addChangeListener(e -> {
                int val = painSlider.getValue();
                painLbl.setText(String.valueOf(val));
                Color c;
                if (val <= 3) c = GREEN;
                else if (val <= 6) c = ORANGE;
                else c = RED_COLOR;
                painLbl.setForeground(c);
                updateRecoveryTime();
            });

            painPanel.add(painSlider, BorderLayout.CENTER);
            painPanel.add(painLbl, BorderLayout.EAST);
            form.add(painPanel, gc);

            // Description
            gc.gridx=0; gc.gridy=2; gc.weightx=0.3;
            JLabel descLbl = new JLabel("Description:");
            descLbl.setFont(FONT_BODY); descLbl.setForeground(TEXT_MUTED);
            form.add(descLbl, gc);
            gc.gridx=1; gc.weightx=0.7;
            descArea = makeTextArea(4, 20);
            form.add(new JScrollPane(descArea), gc);

            // Save button
            gc.gridx=0; gc.gridy=3; gc.gridwidth=2;
            gc.insets = new Insets(16, 4, 4, 4);
            AnimatedButton saveBtn = new AnimatedButton("💾  Save Injury Profile", ACCENT2, ACCENT2.darker());
            saveBtn.setFont(FONT_SUB);
            saveBtn.setForeground(TEXT_PRIMARY);
            saveBtn.setPreferredSize(new Dimension(200, 42));
            saveBtn.addActionListener(e -> saveInjury());
            form.add(saveBtn, gc);

            formCard.add(form, BorderLayout.CENTER);
            twoCol.add(formCard);

            // Right: Recovery info
            JPanel infoCard = makeCard("Recovery Information", "🔄");
            JPanel infoContent = new JPanel();
            infoContent.setLayout(new BoxLayout(infoContent, BoxLayout.Y_AXIS));
            infoContent.setBackground(BG_CARD);
            infoContent.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

            JLabel recTitle = new JLabel("Estimated Recovery Time");
            recTitle.setFont(FONT_SUB); recTitle.setForeground(TEXT_ACCENT);
            recTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoContent.add(recTitle);
            infoContent.add(Box.createRigidArea(new Dimension(0, 8)));

            recovTimeLbl = new JLabel("Select injury type to see estimate");
            recovTimeLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            recovTimeLbl.setForeground(ORANGE);
            recovTimeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoContent.add(recovTimeLbl);
            infoContent.add(Box.createRigidArea(new Dimension(0, 20)));

            // Injury type details
            String[][] injuryInfo = {
                {"Back Pain",     "💆", "Low back pain is very common. Gentle movement and stretching are key to recovery."},
                {"Neck Pain",     "🔄", "Often caused by poor posture. Gentle stretching and ergonomic changes help."},
                {"Knee Injury",   "🦵", "Requires careful rehabilitation. Avoid impact activities until cleared by doctor."},
                {"Shoulder Pain", "💪", "Rest, then gradual mobility work. Avoid overhead activities initially."},
                {"Elbow Pain",    "💪", "Often tennis or golfer's elbow. Ice, rest, and specific stretching recommended."},
                {"Wrist Pain",    "✋", "Common from overuse. Splinting, rest, and gentle stretching aid recovery."},
                {"Ankle Sprain",  "👣", "RICE: Rest, Ice, Compress, Elevate. Gradual weight-bearing as tolerated."},
            };

            for (String[] info : injuryInfo) {
                JPanel row = new RoundedPanel(8, BG_CARD2);
                row.setLayout(new BorderLayout(10, 0));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                JLabel iconL = new JLabel(info[1]);
                iconL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                JPanel textPanel = new JPanel(new GridLayout(2, 1));
                textPanel.setOpaque(false);
                JLabel nameL = new JLabel(info[0]);
                nameL.setFont(FONT_SUB); nameL.setForeground(TEXT_PRIMARY);
                JLabel descL = new JLabel("<html>" + info[2] + "</html>");
                descL.setFont(FONT_SMALL); descL.setForeground(TEXT_MUTED);
                textPanel.add(nameL); textPanel.add(descL);
                row.add(iconL, BorderLayout.WEST);
                row.add(textPanel, BorderLayout.CENTER);
                infoContent.add(row);
                infoContent.add(Box.createRigidArea(new Dimension(0, 6)));
            }

            infoCard.add(infoContent, BorderLayout.CENTER);
            twoCol.add(infoCard);
            content.add(twoCol);

            // Load existing
            if (currentInjury != null) {
                injuryBox.setSelectedItem(currentInjury.type);
                painSlider.setValue(currentInjury.painLevel);
                descArea.setText(currentInjury.description);
                updateRecoveryTime();
            }

            add(darkScroll(content), BorderLayout.CENTER);
        }

        private void updateRecoveryTime() {
            String type = (String) injuryBox.getSelectedItem();
            int pain    = painSlider.getValue();
            InjuryProfile temp = new InjuryProfile(type, pain, "");
            int days = temp.getRecoveryDays();
            recovTimeLbl.setText(days + " – " + (days + 7) + " days estimated");
        }

        private void saveInjury() {
            String type = (String) injuryBox.getSelectedItem();
            int pain    = painSlider.getValue();
            String desc = descArea.getText().trim();
            currentInjury = new InjuryProfile(type, pain, desc);
            saveInjuryData();
            JOptionPane.showMessageDialog(this, "✅ Injury profile saved!\nYour workout plan has been updated.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            app.switchModule("workout");
        }
    }


    // =========================================================================
    //  MODULE 4: WORKOUT PLANNER WITH TIMER
    // =========================================================================
    static class WorkoutPanel extends JPanel {
        private MuscleGuardPro app;
        private List<Exercise> exercises = new ArrayList<>();
        private int currentExerciseIdx = 0;
        private int timerSeconds = 30;
        private Timer exerciseTimer;
        private boolean timerRunning = false;

        // Timer UI
        private CircularProgress timerCircle;
        private JLabel timerLabel, exerciseNameLbl, exerciseEmojiLbl;
        private JLabel instructionsLbl, difficultyLbl, progressLbl;
        private AnimatedButton startBtn, nextBtn, prevBtn;
        private JPanel exerciseListPanel;
        private JProgressBar workoutProgress;

        WorkoutPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel(new BorderLayout(16, 0));
            content.setBackground(BG_DARK);
            content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            // Left: Exercise list
            JPanel leftPanel = new JPanel();
            leftPanel.setBackground(BG_DARK);
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setPreferredSize(new Dimension(300, 0));

            JLabel listTitle = new JLabel("📋  Exercise List");
            listTitle.setFont(FONT_HEADING);
            listTitle.setForeground(TEXT_PRIMARY);
            listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            leftPanel.add(listTitle);

            exerciseListPanel = new JPanel();
            exerciseListPanel.setBackground(BG_DARK);
            exerciseListPanel.setLayout(new BoxLayout(exerciseListPanel, BoxLayout.Y_AXIS));

            JScrollPane listScroll = darkScroll(exerciseListPanel);
            listScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(listScroll);

            content.add(leftPanel, BorderLayout.WEST);

            // Right: Active exercise + timer
            JPanel rightPanel = new JPanel();
            rightPanel.setBackground(BG_DARK);
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

            // Exercise card
            JPanel exCard = makeCard("Current Exercise", "🏋️");
            JPanel exContent = new JPanel(new BorderLayout(16, 0));
            exContent.setBackground(BG_CARD);
            exContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

            // Timer circle
            timerCircle = new CircularProgress(160);
            timerCircle.setProgressColor(GREEN);
            timerCircle.setValue(100);
            timerCircle.setCenterText("30");

            JPanel timerPanel = new JPanel(new BorderLayout());
            timerPanel.setBackground(BG_CARD);
            timerPanel.add(timerCircle, BorderLayout.CENTER);

            timerLabel = new JLabel("30s", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            timerLabel.setForeground(GREEN);
            timerPanel.add(timerLabel, BorderLayout.SOUTH);
            exContent.add(timerPanel, BorderLayout.EAST);

            // Exercise info
            JPanel exInfo = new JPanel();
            exInfo.setLayout(new BoxLayout(exInfo, BoxLayout.Y_AXIS));
            exInfo.setBackground(BG_CARD);

            exerciseEmojiLbl = new JLabel("🏋️");
            exerciseEmojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            exerciseEmojiLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            exerciseNameLbl = new JLabel("Select an exercise to begin");
            exerciseNameLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            exerciseNameLbl.setForeground(TEXT_PRIMARY);
            exerciseNameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            difficultyLbl = new JLabel("Difficulty: —");
            difficultyLbl.setFont(FONT_BODY);
            difficultyLbl.setForeground(TEXT_MUTED);
            difficultyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            instructionsLbl = new JLabel("<html>Set up your injury profile and click Start Workout to begin your recovery exercises.</html>");
            instructionsLbl.setFont(FONT_BODY);
            instructionsLbl.setForeground(TEXT_PRIMARY);
            instructionsLbl.setMaximumSize(new Dimension(500, 100));
            instructionsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            exInfo.add(exerciseEmojiLbl);
            exInfo.add(Box.createRigidArea(new Dimension(0, 8)));
            exInfo.add(exerciseNameLbl);
            exInfo.add(Box.createRigidArea(new Dimension(0, 6)));
            exInfo.add(difficultyLbl);
            exInfo.add(Box.createRigidArea(new Dimension(0, 14)));
            exInfo.add(instructionsLbl);
            exContent.add(exInfo, BorderLayout.CENTER);
            exCard.add(exContent, BorderLayout.CENTER);
            exCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
            rightPanel.add(exCard);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));

            // Controls
            JPanel ctrlCard = makeCard("Controls", "🎮");
            JPanel ctrlContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 16));
            ctrlContent.setBackground(BG_CARD);

            prevBtn = new AnimatedButton("⏮  Previous", BG_CARD2, ACCENT);
            prevBtn.setFont(FONT_BODY); prevBtn.setForeground(TEXT_PRIMARY);
            prevBtn.setPreferredSize(new Dimension(140, 42));
            prevBtn.addActionListener(e -> prevExercise());

            startBtn = new AnimatedButton("▶  Start", GREEN, GREEN.darker());
            startBtn.setFont(FONT_SUB); startBtn.setForeground(BG_DARK);
            startBtn.setPreferredSize(new Dimension(160, 42));
            startBtn.addActionListener(e -> toggleTimer());

            nextBtn = new AnimatedButton("Next  ⏭", BG_CARD2, ACCENT);
            nextBtn.setFont(FONT_BODY); nextBtn.setForeground(TEXT_PRIMARY);
            nextBtn.setPreferredSize(new Dimension(140, 42));
            nextBtn.addActionListener(e -> nextExercise());

            ctrlContent.add(prevBtn);
            ctrlContent.add(startBtn);
            ctrlContent.add(nextBtn);
            ctrlCard.add(ctrlContent, BorderLayout.CENTER);
            ctrlCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            rightPanel.add(ctrlCard);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));

            // Progress
            JPanel progCard = makeCard("Workout Progress", "📊");
            JPanel progContent = new JPanel(new BorderLayout(0, 8));
            progContent.setBackground(BG_CARD);
            progContent.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            workoutProgress = new JProgressBar(0, 100);
            workoutProgress.setValue(0);
            workoutProgress.setStringPainted(true);
            workoutProgress.setBackground(BG_CARD2);
            workoutProgress.setForeground(GREEN);
            workoutProgress.setBorderPainted(false);
            workoutProgress.setPreferredSize(new Dimension(0, 20));

            progressLbl = new JLabel("0 / 0 exercises completed", SwingConstants.CENTER);
            progressLbl.setFont(FONT_BODY);
            progressLbl.setForeground(TEXT_MUTED);

            progContent.add(workoutProgress, BorderLayout.NORTH);
            progContent.add(progressLbl, BorderLayout.CENTER);
            progCard.add(progContent, BorderLayout.CENTER);
            progCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            rightPanel.add(progCard);

            content.add(rightPanel, BorderLayout.CENTER);
            add(content, BorderLayout.CENTER);
        }

        void refresh() {
            if (currentInjury == null) return;
            exercises = ExerciseLibrary.getExercises(currentInjury.type);
            currentExerciseIdx = 0;
            buildExerciseList();
            if (!exercises.isEmpty()) showExercise(0);
            updateProgress();
        }

        private void buildExerciseList() {
            exerciseListPanel.removeAll();
            for (int i = 0; i < exercises.size(); i++) {
                Exercise ex = exercises.get(i);
                final int idx = i;
                JPanel row = new RoundedPanel(8, i == currentExerciseIdx ? BG_CARD2 : BG_CARD);
                row.setLayout(new BorderLayout(8, 0));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(i == currentExerciseIdx ? ACCENT : BORDER_COLOR),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel iconL = new JLabel(ex.emoji);
                iconL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                JLabel nameL = new JLabel("<html><b>" + ex.name + "</b><br><font size='2'>" + ex.difficulty + " • " + ex.durationSec + "s</font></html>");
                nameL.setFont(FONT_BODY);
                nameL.setForeground(TEXT_PRIMARY);
                JLabel checkL = new JLabel(ex.completed ? "✅" : "○");
                checkL.setFont(FONT_BODY);
                checkL.setForeground(ex.completed ? GREEN : TEXT_MUTED);

                row.add(iconL, BorderLayout.WEST);
                row.add(nameL, BorderLayout.CENTER);
                row.add(checkL, BorderLayout.EAST);
                row.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        stopTimer(); currentExerciseIdx = idx;
                        buildExerciseList(); showExercise(idx);
                    }
                });
                exerciseListPanel.add(row);
                exerciseListPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
            exerciseListPanel.revalidate();
            exerciseListPanel.repaint();
        }

        private void showExercise(int idx) {
            if (idx < 0 || idx >= exercises.size()) return;
            Exercise ex = exercises.get(idx);
            exerciseEmojiLbl.setText(ex.emoji);
            exerciseNameLbl.setText(ex.name);
            difficultyLbl.setText("Difficulty: " + ex.difficulty + "  |  Duration: " + ex.durationSec + " seconds");
            instructionsLbl.setText("<html>" + ex.instructions + "</html>");
            timerSeconds = ex.durationSec;
            timerLabel.setText(timerSeconds + "s");
            timerCircle.setValue(100);
            timerCircle.setProgressColor(GREEN);
            startBtn.setText("▶  Start");
        }

        private void toggleTimer() {
            if (timerRunning) { stopTimer(); }
            else { startTimer(); }
        }

        private void startTimer() {
            if (currentExerciseIdx >= exercises.size()) return;
            timerRunning = true;
            startBtn.setText("⏸  Pause");
            final int totalSec = exercises.get(currentExerciseIdx).durationSec;

            exerciseTimer = new Timer(1000, null);
            exerciseTimer.addActionListener(e -> {
                timerSeconds--;
                timerLabel.setText(timerSeconds + "s");
                timerCircle.setValue((int)((timerSeconds * 100.0) / totalSec));

                // Color change: green→orange→red
                if (timerSeconds > totalSec * 0.6)
                    timerCircle.setProgressColor(GREEN);
                else if (timerSeconds > totalSec * 0.3)
                    timerCircle.setProgressColor(ORANGE);
                else
                    timerCircle.setProgressColor(RED_COLOR);

                if (timerSeconds <= 0) {
                    exerciseTimer.stop();
                    timerRunning = false;
                    exerciseComplete();
                }
            });
            exerciseTimer.start();
        }

        private void stopTimer() {
            timerRunning = false;
            if (exerciseTimer != null) exerciseTimer.stop();
            startBtn.setText("▶  Start");
        }

        private void exerciseComplete() {
            if (currentExerciseIdx < exercises.size()) {
                exercises.get(currentExerciseIdx).completed = true;
                tracker.totalExercises++;
            }
            timerLabel.setText("Done! ✅");
            timerCircle.setValue(100);
            timerCircle.setProgressColor(GREEN);
            startBtn.setText("▶  Start");
            updateProgress();
            buildExerciseList();

            // Auto-advance after 2 seconds
            Timer advanceTimer = new Timer(2000, e -> {
                nextExercise();
                ((Timer)e.getSource()).stop();
            });
            advanceTimer.start();
        }

        private void nextExercise() {
            stopTimer();
            if (currentExerciseIdx < exercises.size() - 1) {
                currentExerciseIdx++;
                buildExerciseList();
                showExercise(currentExerciseIdx);
            } else {
                JOptionPane.showMessageDialog(this,
                    "🎉 Workout Complete!\n\nYou finished all " + exercises.size() + " exercises!\nGreat job!",
                    "Workout Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void prevExercise() {
            stopTimer();
            if (currentExerciseIdx > 0) {
                currentExerciseIdx--;
                buildExerciseList();
                showExercise(currentExerciseIdx);
            }
        }

        private void updateProgress() {
            long done = exercises.stream().filter(e -> e.completed).count();
            int pct = exercises.isEmpty() ? 0 : (int)(done * 100 / exercises.size());
            workoutProgress.setValue(pct);
            workoutProgress.setString(pct + "% complete");
            progressLbl.setText(done + " / " + exercises.size() + " exercises completed");
        }
    }


    // =========================================================================
    //  MODULE 5: DIET PLANNER
    // =========================================================================
    static class DietPanel extends JPanel {
        private MuscleGuardPro app;

        DietPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel title = new JLabel("🥗  Diet & Nutrition Planner");
            title.setFont(FONT_TITLE); title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(title);
            content.add(Box.createRigidArea(new Dimension(0, 6)));
            JLabel sub = new JLabel("Anti-inflammatory foods and personalized meal plans for injury recovery");
            sub.setFont(FONT_BODY); sub.setForeground(TEXT_MUTED);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(sub);
            content.add(Box.createRigidArea(new Dimension(0, 20)));

            // Calorie summary
            JPanel calsCard = buildCalorieCard();
            calsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            calsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            content.add(calsCard);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Meal plan
            JPanel mealRow = new JPanel(new GridLayout(2, 2, 14, 14));
            mealRow.setBackground(BG_DARK);
            mealRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
            mealRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            mealRow.add(buildMealCard("🌅  Breakfast", getBreakfastItems()));
            mealRow.add(buildMealCard("☀️  Lunch", getLunchItems()));
            mealRow.add(buildMealCard("🌙  Dinner", getDinnerItems()));
            mealRow.add(buildMealCard("🍎  Snacks", getSnackItems()));
            content.add(mealRow);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Anti-inflammatory foods
            JPanel antiCard = buildAntiInflammatoryCard();
            antiCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            antiCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
            content.add(antiCard);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Hydration reminder
            JPanel hydroCard = buildHydrationCard();
            hydroCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            hydroCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            content.add(hydroCard);

            add(darkScroll(content), BorderLayout.CENTER);
        }

        private JPanel buildCalorieCard() {
            JPanel card = makeCard("Daily Calorie Targets", "🔥");
            JPanel cc = new JPanel(new GridLayout(1, 4, 12, 0));
            cc.setBackground(BG_CARD);
            cc.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            double bmr  = currentUser != null ? currentUser.getBMR()  : 1500;
            double tdee = currentUser != null ? currentUser.getTDEE() : 2100;
            double goal = tdee * 0.9; // slight deficit for recovery
            double protein = (currentUser != null ? currentUser.weightKg : 70) * 2.0;

            addCalorieBox(cc, "BMR",          String.format("%.0f", bmr),  "kcal/day", TEXT_MUTED);
            addCalorieBox(cc, "TDEE",         String.format("%.0f", tdee), "kcal/day", TEXT_ACCENT);
            addCalorieBox(cc, "Recovery Goal",String.format("%.0f", goal), "kcal/day", GREEN);
            addCalorieBox(cc, "Protein",      String.format("%.0fg", protein), "/day", ORANGE);

            card.add(cc, BorderLayout.CENTER);
            return card;
        }

        private void addCalorieBox(JPanel parent, String label, String value, String unit, Color vc) {
            JPanel box = new RoundedPanel(8, BG_CARD2);
            box.setLayout(new GridLayout(3, 1, 0, 2));
            box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            JLabel lbl = new JLabel(label, SwingConstants.CENTER);
            lbl.setFont(FONT_SMALL); lbl.setForeground(TEXT_MUTED);
            JLabel val = new JLabel(value, SwingConstants.CENTER);
            val.setFont(new Font("Segoe UI", Font.BOLD, 18)); val.setForeground(vc);
            JLabel unt = new JLabel(unit, SwingConstants.CENTER);
            unt.setFont(FONT_SMALL); unt.setForeground(TEXT_MUTED);
            box.add(lbl); box.add(val); box.add(unt);
            parent.add(box);
        }

        private JPanel buildMealCard(String title, String[] items) {
            JPanel card = makeCard(title, "");
            JPanel cc = new JPanel();
            cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));
            cc.setBackground(BG_CARD);
            cc.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            for (String item : items) {
                JLabel lbl = new JLabel("• " + item);
                lbl.setFont(FONT_BODY);
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                cc.add(lbl);
            }
            card.add(cc, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildAntiInflammatoryCard() {
            JPanel card = makeCard("Anti-Inflammatory Foods for Recovery", "🌿");
            JPanel cc = new JPanel(new GridLayout(2, 4, 10, 10));
            cc.setBackground(BG_CARD);
            cc.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            String[][] foods = {
                {"🐟", "Fatty Fish",   "Omega-3 reduces inflammation"},
                {"🫐", "Blueberries",  "Antioxidants for healing"},
                {"🧄", "Garlic",       "Natural anti-inflammatory"},
                {"🫚", "Olive Oil",    "Healthy fats, oleocanthal"},
                {"🥦", "Broccoli",     "Sulforaphane, Vitamin C"},
                {"🍵", "Green Tea",    "EGCG reduces oxidative stress"},
                {"🫚", "Turmeric",     "Curcumin: powerful anti-inflam."},
                {"🥑", "Avocado",      "Healthy fats, carotenoids"},
            };
            for (String[] food : foods) {
                JPanel box = new RoundedPanel(8, BG_CARD2);
                box.setLayout(new BorderLayout(8, 0));
                box.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
                JLabel iconL = new JLabel(food[0]);
                iconL.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);
                JLabel nl = new JLabel(food[1]);
                nl.setFont(FONT_SUB); nl.setForeground(TEXT_PRIMARY);
                JLabel dl = new JLabel(food[2]);
                dl.setFont(FONT_SMALL); dl.setForeground(TEXT_MUTED);
                info.add(nl); info.add(dl);
                box.add(iconL, BorderLayout.WEST);
                box.add(info, BorderLayout.CENTER);
                cc.add(box);
            }
            card.add(cc, BorderLayout.CENTER);
            return card;
        }

        private JPanel buildHydrationCard() {
            JPanel card = makeCard("💧  Hydration Reminder", "");
            JPanel cc = new JPanel(new BorderLayout());
            cc.setBackground(BG_CARD);
            cc.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            double water = currentUser != null ? currentUser.weightKg * 0.033 : 2.0;
            JLabel lbl = new JLabel(String.format("Daily Water Goal: %.1f liters | Drink a glass every 1-2 hours | " +
                "Staying hydrated reduces inflammation and aids tissue repair.", water));
            lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_ACCENT);
            cc.add(lbl);
            card.add(cc, BorderLayout.CENTER);
            return card;
        }

        private String[] getBreakfastItems() {
            return new String[]{
                "Oatmeal with blueberries & walnuts",
                "Greek yogurt with honey & flaxseeds",
                "Whole grain toast with avocado",
                "Turmeric golden milk smoothie",
                "Boiled eggs with spinach",
            };
        }
        private String[] getLunchItems() {
            return new String[]{
                "Grilled salmon with quinoa & veggies",
                "Lentil soup with whole grain bread",
                "Chicken salad with olive oil dressing",
                "Brown rice with steamed broccoli",
                "Chickpea bowl with leafy greens",
            };
        }
        private String[] getDinnerItems() {
            return new String[]{
                "Baked chicken with roasted sweet potato",
                "Stir-fried tofu with mixed vegetables",
                "Grilled fish with sautéed kale",
                "Bean and vegetable curry with rice",
                "Turkey meatballs with zucchini noodles",
            };
        }
        private String[] getSnackItems() {
            return new String[]{
                "Handful of almonds or walnuts",
                "Apple slices with almond butter",
                "Carrot sticks with hummus",
                "Green tea with a small dark chocolate",
                "Tart cherry juice (reduces muscle soreness)",
            };
        }

        void refresh() {
            removeAll();
            build();
            revalidate();
            repaint();
        }
    }


    // =========================================================================
    //  MODULE 6: MEDICAL TRACKER
    // =========================================================================
    static class MedicalPanel extends JPanel {
        private MuscleGuardPro app;
        private JPanel medsListPanel;
        private JTextField medNameF, medTimeF, medNotesF;
        private JTextArea diaryArea;
        private JComboBox<String> diaryPainBox;

        MedicalPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel title = new JLabel("⚕  Medical Tracker");
            title.setFont(FONT_TITLE); title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(title);
            content.add(Box.createRigidArea(new Dimension(0, 20)));

            // Tabs: Medications, Diary, First Aid
            JTabbedPane tabs = new JTabbedPane();
            tabs.setBackground(BG_CARD);
            tabs.setForeground(TEXT_PRIMARY);
            tabs.setFont(FONT_BODY);
            tabs.setBorder(null);
            tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            tabs.setAlignmentX(Component.LEFT_ALIGNMENT);

            tabs.addTab("💊 Medications", buildMedicationsTab());
            tabs.addTab("📔 Symptom Diary", buildDiaryTab());
            tabs.addTab("🚑 First Aid", buildFirstAidTab());
            tabs.addTab("⚠️ Warning Signs", buildWarningSignsTab());

            content.add(tabs);
            add(darkScroll(content), BorderLayout.CENTER);
        }

        private JPanel buildMedicationsTab() {
            JPanel panel = new JPanel(new BorderLayout(14, 14));
            panel.setBackground(BG_DARK);
            panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            // Add medication form
            JPanel addCard = makeCard("Add Medication", "➕");
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(BG_CARD);
            form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6, 4, 6, 4);
            gc.fill = GridBagConstraints.HORIZONTAL;

            medNameF  = makeTextField("Medication name");
            medTimeF  = makeTextField("Time (e.g. 8:00 AM)");
            medNotesF = makeTextField("Notes or dosage");

            String[][] fs = {{"Medication Name:", ""}, {"Reminder Time:", ""}, {"Dosage Notes:", ""}};
            JTextField[] flds = {medNameF, medTimeF, medNotesF};
            for (int i = 0; i < fs.length; i++) {
                gc.gridx=0; gc.gridy=i; gc.weightx=0.3;
                JLabel lbl = new JLabel(fs[i][0]);
                lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_MUTED);
                form.add(lbl, gc);
                gc.gridx=1; gc.weightx=0.7;
                form.add(flds[i], gc);
            }
            gc.gridx=0; gc.gridy=3; gc.gridwidth=2; gc.insets=new Insets(12,4,4,4);
            AnimatedButton addBtn = new AnimatedButton("➕  Add Reminder", ACCENT, ACCENT.darker());
            addBtn.setFont(FONT_BODY); addBtn.setForeground(TEXT_PRIMARY);
            addBtn.setPreferredSize(new Dimension(180, 38));
            addBtn.addActionListener(e -> addMedication());
            form.add(addBtn, gc);
            addCard.add(form, BorderLayout.CENTER);
            panel.add(addCard, BorderLayout.NORTH);

            // Meds list
            JPanel listCard = makeCard("Medication Schedule", "📋");
            medsListPanel = new JPanel();
            medsListPanel.setLayout(new BoxLayout(medsListPanel, BoxLayout.Y_AXIS));
            medsListPanel.setBackground(BG_CARD);
            refreshMedsList();
            listCard.add(medsListPanel, BorderLayout.CENTER);
            panel.add(listCard, BorderLayout.CENTER);
            return panel;
        }

        private void refreshMedsList() {
            medsListPanel.removeAll();
            medsListPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            if (tracker.medications.isEmpty()) {
                JLabel empty = new JLabel("No medications added yet.");
                empty.setFont(FONT_BODY); empty.setForeground(TEXT_MUTED);
                medsListPanel.add(empty);
            } else {
                for (Medication m : tracker.medications) {
                    JPanel row = new RoundedPanel(8, BG_CARD2);
                    row.setLayout(new BorderLayout(10, 0));
                    row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                    ));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                    JLabel nameL = new JLabel("💊  " + m.name);
                    nameL.setFont(FONT_SUB); nameL.setForeground(TEXT_PRIMARY);
                    JLabel timeL = new JLabel("⏰ " + m.time);
                    timeL.setFont(FONT_BODY); timeL.setForeground(ORANGE);
                    JLabel noteL = new JLabel(m.notes);
                    noteL.setFont(FONT_SMALL); noteL.setForeground(TEXT_MUTED);
                    JCheckBox check = new JCheckBox("Taken");
                    check.setBackground(BG_CARD2); check.setForeground(GREEN);
                    check.setFont(FONT_SMALL); check.setSelected(m.takenToday);
                    check.addActionListener(e -> { m.takenToday = check.isSelected(); tracker.save(); });

                    JPanel info = new JPanel(new GridLayout(2, 1));
                    info.setOpaque(false);
                    info.add(nameL); info.add(noteL);
                    row.add(info, BorderLayout.CENTER);
                    row.add(timeL, BorderLayout.EAST);
                    row.add(check, BorderLayout.WEST);
                    medsListPanel.add(row);
                    medsListPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                }
            }
            medsListPanel.revalidate();
            medsListPanel.repaint();
        }

        private void addMedication() {
            String name  = medNameF.getText().trim();
            String time  = medTimeF.getText().trim();
            String notes = medNotesF.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter medication name.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tracker.medications.add(new Medication(name, time, notes));
            tracker.save();
            medNameF.setText(""); medTimeF.setText(""); medNotesF.setText("");
            refreshMedsList();
        }

        private JPanel buildDiaryTab() {
            JPanel panel = new JPanel(new BorderLayout(14, 14));
            panel.setBackground(BG_DARK);
            panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            // Add entry
            JPanel addCard = makeCard("Log Today's Symptoms", "✍️");
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(BG_CARD);
            form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(6,4,6,4); gc.fill = GridBagConstraints.HORIZONTAL;

            gc.gridx=0; gc.gridy=0; gc.weightx=0.3;
            form.add(fieldLabel("Pain Level (1-10):"), gc);
            gc.gridx=1; gc.weightx=0.7;
            diaryPainBox = makeCombo(new String[]{"1","2","3","4","5","6","7","8","9","10"});
            diaryPainBox.setSelectedItem("5");
            form.add(diaryPainBox, gc);

            gc.gridx=0; gc.gridy=1; gc.weightx=0.3;
            form.add(fieldLabel("Symptoms:"), gc);
            gc.gridx=1; gc.weightx=0.7;
            diaryArea = makeTextArea(3, 30);
            form.add(new JScrollPane(diaryArea), gc);

            gc.gridx=0; gc.gridy=2; gc.gridwidth=2; gc.insets=new Insets(12,4,4,4);
            AnimatedButton logBtn = new AnimatedButton("📝  Log Entry", ACCENT2, ACCENT2.darker());
            logBtn.setFont(FONT_BODY); logBtn.setForeground(TEXT_PRIMARY);
            logBtn.setPreferredSize(new Dimension(160, 38));
            logBtn.addActionListener(e -> logDiaryEntry());
            form.add(logBtn, gc);
            addCard.add(form, BorderLayout.CENTER);
            panel.add(addCard, BorderLayout.NORTH);

            // History
            JPanel histCard = makeCard("Diary History", "📖");
            JPanel histContent = new JPanel();
            histContent.setLayout(new BoxLayout(histContent, BoxLayout.Y_AXIS));
            histContent.setBackground(BG_CARD);
            histContent.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            for (DiaryEntry e : tracker.diaryEntries) {
                JPanel row = new RoundedPanel(8, BG_CARD2);
                row.setLayout(new BorderLayout(8, 0));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                Color pc = e.painLevel <= 3 ? GREEN : e.painLevel <= 6 ? ORANGE : RED_COLOR;
                JLabel dateL = new JLabel("📅 " + e.date);
                dateL.setFont(FONT_SMALL); dateL.setForeground(TEXT_MUTED);
                JLabel painL = new JLabel("Pain: " + e.painLevel + "/10");
                painL.setFont(FONT_SUB); painL.setForeground(pc);
                JLabel sympL = new JLabel(e.symptoms.length() > 60 ? e.symptoms.substring(0,60)+"..." : e.symptoms);
                sympL.setFont(FONT_SMALL); sympL.setForeground(TEXT_MUTED);

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);
                info.add(sympL); info.add(dateL);
                row.add(painL, BorderLayout.WEST);
                row.add(info, BorderLayout.CENTER);
                histContent.add(row);
                histContent.add(Box.createRigidArea(new Dimension(0, 4)));
            }
            histCard.add(histContent, BorderLayout.CENTER);
            panel.add(histCard, BorderLayout.CENTER);
            return panel;
        }

        private void logDiaryEntry() {
            int pain = parseInt((String)diaryPainBox.getSelectedItem(), 5);
            String symptoms = diaryArea.getText().trim();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            tracker.addDiaryEntry(new DiaryEntry(date, pain, symptoms.isEmpty() ? "No symptoms noted" : symptoms, "Okay"));
            diaryArea.setText("");
            JOptionPane.showMessageDialog(this, "✅ Diary entry saved!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        }

        private JPanel buildFirstAidTab() {
            JPanel panel = new JPanel();
            panel.setBackground(BG_DARK);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            String[][] tips = {
                {"🧊 RICE Protocol",  "Rest – Stop activity and rest the injured area.\n" +
                    "Ice – Apply ice pack for 15-20 min every 1-2 hours (first 48h).\n" +
                    "Compress – Use elastic bandage to reduce swelling.\n" +
                    "Elevate – Raise injured limb above heart level."},
                {"💊 Pain Management", "Over-the-counter NSAIDs (ibuprofen, naproxen) reduce inflammation.\n" +
                    "Acetaminophen for pain relief without anti-inflammatory effect.\n" +
                    "Always follow recommended dosage. Consult doctor if unsure."},
                {"🔥 Heat vs Ice",    "Ice (first 48-72 hours): Reduces swelling and numbs pain.\n" +
                    "Heat (after 72 hours): Relaxes muscles, increases blood flow.\n" +
                    "Never apply directly to skin — use a cloth barrier."},
                {"🩹 Wound Care",     "Clean wounds thoroughly with soap and water.\n" +
                    "Apply antiseptic and cover with sterile dressing.\n" +
                    "Change dressing daily. Watch for signs of infection."},
                {"🫁 Breathing",      "Deep breathing reduces pain perception and stress.\n" +
                    "4-7-8 technique: Inhale 4s, hold 7s, exhale 8s.\n" +
                    "Helps with muscle tension and recovery."},
            };

            for (String[] tip : tips) {
                JPanel card = makeCard(tip[0], "");
                JPanel cc = new JPanel(new BorderLayout());
                cc.setBackground(BG_CARD);
                cc.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                JLabel lbl = new JLabel("<html>" + tip[1].replace("\n", "<br>") + "</html>");
                lbl.setFont(FONT_BODY); lbl.setForeground(TEXT_PRIMARY);
                cc.add(lbl);
                card.add(cc, BorderLayout.CENTER);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(card);
                panel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
            return panel;
        }

        private JPanel buildWarningSignsTab() {
            JPanel panel = new JPanel();
            panel.setBackground(BG_DARK);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

            JLabel warn = new JLabel("⚠️  Seek immediate medical attention if you experience:");
            warn.setFont(FONT_HEADING); warn.setForeground(RED_COLOR);
            warn.setAlignmentX(Component.LEFT_ALIGNMENT);
            warn.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
            panel.add(warn);

            String[] signs = {
                "🔴  Numbness or tingling that doesn't go away",
                "🔴  Loss of bladder or bowel control",
                "🔴  Severe pain that doesn't improve with rest",
                "🔴  Visible bone deformity or open fracture",
                "🔴  Chest pain, shortness of breath during exercise",
                "🔴  Sudden severe headache",
                "🔴  Confusion, dizziness, or loss of consciousness",
                "🔴  Signs of infection: fever, increasing redness, warmth, pus",
                "🟡  Pain that worsens at night (may indicate serious pathology)",
                "🟡  Unintended weight loss with back pain",
                "🟡  Pain in multiple joints simultaneously",
                "🟡  Significant swelling that doesn't reduce after 48h",
            };

            for (String sign : signs) {
                JPanel row = new RoundedPanel(6, BG_CARD);
                row.setLayout(new BorderLayout());
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0,
                        sign.startsWith("🔴") ? RED_COLOR : ORANGE),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                JLabel lbl = new JLabel(sign);
                lbl.setFont(FONT_BODY);
                lbl.setForeground(sign.startsWith("🔴") ? new Color(0xffeaea) : new Color(0xfff3cd));
                row.add(lbl);
                panel.add(row);
                panel.add(Box.createRigidArea(new Dimension(0, 4)));
            }
            return panel;
        }

        private JLabel fieldLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(FONT_BODY); l.setForeground(TEXT_MUTED);
            return l;
        }
    }


    // =========================================================================
    //  MODULE 7: PROGRESS TRACKING
    // =========================================================================
    static class ProgressPanel extends JPanel {
        private MuscleGuardPro app;
        private LineChart painChart;
        private CircularProgress overallCircle;

        ProgressPanel(MuscleGuardPro app) {
            this.app = app;
            setBackground(BG_DARK);
            setLayout(new BorderLayout());
            build();
        }

        private void build() {
            JPanel content = new JPanel();
            content.setBackground(BG_DARK);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel title = new JLabel("📊  Progress Tracker");
            title.setFont(FONT_TITLE); title.setForeground(TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(title);
            content.add(Box.createRigidArea(new Dimension(0, 20)));

            // Top row: overall circle + streak + exercises
            JPanel topRow = new JPanel(new GridLayout(1, 3, 14, 0));
            topRow.setBackground(BG_DARK);
            topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
            topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel overallCard = makeCard("Overall Recovery", "🔄");
            JPanel overallContent = new JPanel(new BorderLayout());
            overallContent.setBackground(BG_CARD);
            overallContent.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            overallCircle = new CircularProgress(140);
            overallCircle.setProgressColor(GREEN);
            overallCircle.setValue((int) tracker.getRecoveryPercent());
            JPanel cw = new JPanel(new FlowLayout(FlowLayout.CENTER));
            cw.setBackground(BG_CARD);
            cw.add(overallCircle);
            overallContent.add(cw);
            overallCard.add(overallContent, BorderLayout.CENTER);
            topRow.add(overallCard);

            topRow.add(makeStatCard("🔥  Day Streak",   String.valueOf(tracker.streakDays),   "consecutive days"));
            topRow.add(makeStatCard("💪  Exercises",    String.valueOf(tracker.totalExercises), "completed total"));

            content.add(topRow);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Pain level chart
            JPanel chartCard = makeCard("Pain Level Trend (Last 10 entries)", "📈");
            painChart = new LineChart("Pain Level Over Time (1-10 scale)");
            painChart.setPreferredSize(new Dimension(800, 200));
            painChart.setMinimumSize(new Dimension(400, 180));
            painChart.lineColor = ACCENT2;
            updatePainChart();
            chartCard.add(painChart, BorderLayout.CENTER);
            chartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
            chartCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(chartCard);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Export + Check-in
            JPanel actionsRow = new JPanel(new GridLayout(1, 2, 14, 0));
            actionsRow.setBackground(BG_DARK);
            actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            actionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            AnimatedButton checkIn = new AnimatedButton("✅  Daily Check-In", GREEN, GREEN.darker());
            checkIn.setFont(FONT_BODY); checkIn.setForeground(BG_DARK);
            checkIn.addActionListener(e -> doCheckIn());

            AnimatedButton export = new AnimatedButton("📄  Export Report", ACCENT, ACCENT.darker());
            export.setFont(FONT_BODY); export.setForeground(TEXT_PRIMARY);
            export.addActionListener(e -> exportReport());

            actionsRow.add(checkIn);
            actionsRow.add(export);
            content.add(actionsRow);
            content.add(Box.createRigidArea(new Dimension(0, 16)));

            // Diary summary table
            JPanel diaryCard = makeCard("Recent Pain Log", "📋");
            JPanel diaryContent = new JPanel();
            diaryContent.setLayout(new BoxLayout(diaryContent, BoxLayout.Y_AXIS));
            diaryContent.setBackground(BG_CARD);
            diaryContent.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            // Header
            JPanel hdr = new JPanel(new GridLayout(1, 3));
            hdr.setBackground(BG_CARD2);
            hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            hdr.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            for (String h : new String[]{"Date", "Pain Level", "Symptoms"}) {
                JLabel hl = new JLabel(h); hl.setFont(FONT_SUB); hl.setForeground(TEXT_ACCENT);
                hdr.add(hl);
            }
            diaryContent.add(hdr);

            List<DiaryEntry> recent = tracker.diaryEntries;
            if (recent.isEmpty()) {
                JLabel el = new JLabel("  No diary entries yet. Log your symptoms in the Medical Tracker.");
                el.setFont(FONT_BODY); el.setForeground(TEXT_MUTED);
                el.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                diaryContent.add(el);
            } else {
                int start = Math.max(0, recent.size() - 10);
                for (int i = recent.size()-1; i >= start; i--) {
                    DiaryEntry e = recent.get(i);
                    JPanel row = new JPanel(new GridLayout(1, 3));
                    row.setBackground(i % 2 == 0 ? BG_CARD : BG_CARD2);
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
                    row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    Color pc = e.painLevel <= 3 ? GREEN : e.painLevel <= 6 ? ORANGE : RED_COLOR;
                    JLabel dl = new JLabel(e.date); dl.setFont(FONT_BODY); dl.setForeground(TEXT_MUTED);
                    JLabel pl = new JLabel(e.painLevel + " / 10"); pl.setFont(FONT_SUB); pl.setForeground(pc);
                    JLabel sl = new JLabel(e.symptoms.length()>50?e.symptoms.substring(0,50)+"...":e.symptoms);
                    sl.setFont(FONT_SMALL); sl.setForeground(TEXT_MUTED);
                    row.add(dl); row.add(pl); row.add(sl);
                    diaryContent.add(row);
                }
            }
            diaryCard.add(diaryContent, BorderLayout.CENTER);
            diaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(diaryCard);

            add(darkScroll(content), BorderLayout.CENTER);
        }

        private JPanel makeStatCard(String title, String value, String sub) {
            JPanel card = makeCard(title, "");
            JPanel cc = new JPanel(new GridLayout(3, 1, 0, 4));
            cc.setBackground(BG_CARD);
            cc.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
            JLabel vl = new JLabel(value, SwingConstants.CENTER);
            vl.setFont(new Font("Segoe UI", Font.BOLD, 42));
            vl.setForeground(ORANGE);
            JLabel sl = new JLabel(sub, SwingConstants.CENTER);
            sl.setFont(FONT_BODY); sl.setForeground(TEXT_MUTED);
            cc.add(new JLabel()); cc.add(vl); cc.add(sl);
            card.add(cc, BorderLayout.CENTER);
            return card;
        }

        private void updatePainChart() {
            List<Integer> vals = new ArrayList<>();
            List<String>  lbls = new ArrayList<>();
            List<DiaryEntry> entries = tracker.diaryEntries;
            int start = Math.max(0, entries.size() - 10);
            for (int i = start; i < entries.size(); i++) {
                DiaryEntry e = entries.get(i);
                vals.add(e.painLevel);
                String d = e.date.length() >= 10 ? e.date.substring(5) : e.date;
                lbls.add(d);
            }
            painChart.setData(vals, lbls);
        }

        private void doCheckIn() {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (!tracker.completedDates.contains(today)) {
                tracker.completedDates.add(today);
                tracker.streakDays++;
                tracker.save();
                overallCircle.setValue((int) tracker.getRecoveryPercent());
                JOptionPane.showMessageDialog(this,
                    "✅ Daily check-in recorded!\n🔥 Streak: " + tracker.streakDays + " days!",
                    "Check-In Complete", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                JOptionPane.showMessageDialog(this,
                    "You already checked in today!\n🔥 Current Streak: " + tracker.streakDays + " days",
                    "Already Checked In", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void exportReport() {
            String filename = "recovery_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
            try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
                pw.println("╔═══════════════════════════════════════════════════╗");
                pw.println("║          MUSCLEGUARD PRO — RECOVERY REPORT        ║");
                pw.println("╚═══════════════════════════════════════════════════╝");
                pw.println("Generated: " + new Date());
                pw.println();
                if (currentUser != null) {
                    pw.println("── USER PROFILE ──────────────────────────────────");
                    pw.println("Name:    " + currentUser.name);
                    pw.println("Age:     " + currentUser.age);
                    pw.println("Gender:  " + currentUser.gender);
                    pw.println("Weight:  " + currentUser.weightKg + " kg");
                    pw.println("Height:  " + currentUser.heightCm + " cm");
                    pw.printf("BMI:     %.1f (%s)%n", currentUser.getBMI(), currentUser.getBMICategory());
                    pw.printf("BMR:     %.0f kcal/day%n", currentUser.getBMR());
                    pw.println();
                }
                if (currentInjury != null) {
                    pw.println("── INJURY PROFILE ─────────────────────────────────");
                    pw.println("Type:        " + currentInjury.type);
                    pw.println("Pain Level:  " + currentInjury.painLevel + "/10");
                    pw.println("Description: " + currentInjury.description);
                    pw.println("Recovery Est: " + currentInjury.getRecoveryDays() + " days");
                    pw.println();
                }
                pw.println("── PROGRESS SUMMARY ───────────────────────────────");
                pw.printf("Overall Recovery: %.1f%%%n", tracker.getRecoveryPercent());
                pw.println("Day Streak:       " + tracker.streakDays);
                pw.println("Total Exercises:  " + tracker.totalExercises);
                pw.println();
                pw.println("── PAIN DIARY ─────────────────────────────────────");
                pw.printf("%-12s %-10s %s%n", "Date", "Pain Level", "Symptoms");
                pw.println("──────────────────────────────────────────────────");
                for (DiaryEntry e : tracker.diaryEntries) {
                    pw.printf("%-12s %-10d %s%n", e.date, e.painLevel, e.symptoms);
                }
                pw.println();
                pw.println("═══════════════════════════════════════════════════");
                pw.println("          Generated by MuscleGuard Pro v1.0         ");
                pw.println("═══════════════════════════════════════════════════");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                "✅ Report exported to:\n" + filename,
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        }

        void refresh() {
            removeAll();
            build();
            revalidate();
            repaint();
        }
    }

} // end MuscleGuardPro
