package org.sfedu.pathfinder;

import org.sfedu.pathfinder.algorithm.AStarAlgorithm;
import org.sfedu.pathfinder.io.GeoJsonExporter;
import org.sfedu.pathfinder.io.OSMDataReader;
import org.sfedu.pathfinder.model.Node;
import org.sfedu.pathfinder.model.RoadsType;
import org.sfedu.pathfinder.service.PathfindingService;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static PathfindingService service;
    private static String currentFilePath = "";
    private static RoadsType currentRoadsType = null;

    private static final double MIN_LAT = -90.0;
    private static final double MAX_LAT = 90.0;
    private static final double MIN_LON = -180.0;
    private static final double MAX_LON = 180.0;

    public static void main(String[] args) {
        service = new PathfindingService(new AStarAlgorithm());
        
        while (true) {
            clearConsole();
            printMainMenu();
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> loadGraphFromFile();
                case "2" -> reloadGraphWithDifferentType();
                case "3" -> saveCurrentGraph();
                case "4" -> findPath();
                case "5" -> {
                    System.out.println("\n=== Завершение работы ===");
                    System.out.println("До свидания!");
                    return;
                }
                default -> showErrorAndWait("Неверный выбор");
            }
        }
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("=== Поиск пути на карте ===");
        System.out.println("\nТекущее состояние:");
        System.out.println("  Файл графа: " + (currentFilePath.isEmpty() ? "Не загружен" : currentFilePath));
        System.out.println("  Тип дорог: " + (currentRoadsType == null ? "Не выбран" : 
            (currentRoadsType == RoadsType.Vehicle ? "Автомобильные" : "Пешеходные")));
        
        System.out.println("\nДоступные действия:");
        System.out.println("1. Загрузить граф из файла");
        System.out.println("2. Поменять тип дорог");
        System.out.println("3. Сохранить текущий граф в .geojson");
        System.out.println("4. Найти путь");
        System.out.println("5. Выход");
        
        System.out.print("\nВыберите действие (1-5): ");
    }

    private static void showErrorAndWait(String message) {
        System.out.println("\n" + message);
        System.out.println("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private static boolean checkGraphLoaded() {
        if (currentFilePath.isEmpty() || currentRoadsType == null) {
            showErrorAndWait("Сначала необходимо загрузить файл графа и выбрать тип дорог");
            return false;
        }

        return true;
    }

    private static RoadsType selectRoadType(String currentType) {
        clearConsole();
        System.out.println("=== Выбор типа дорог ===");

        if (currentType != null)
            System.out.println("Текущий тип: " + currentType);

        System.out.println("\nДоступные типы:");
        System.out.println("1. Автомобильные");
        System.out.println("2. Пешеходные");
        System.out.print("\nВыберите тип (1 или 2): ");

        String typeChoice = scanner.nextLine().trim();

        return switch (typeChoice) {
            case "1" -> RoadsType.Vehicle;
            case "2" -> RoadsType.Pedestrian;
            default -> {
                showErrorAndWait("Неверный выбор типа дорог");
                yield null;
            }
        };
    }

    private static double[] readCoordinates(String pointName) {
        while (true) {
            System.out.println("\nВведите " + pointName + " координаты (широта, долгота):");
            System.out.println("Пример: 47.2345, 39.7123");
            System.out.print("> ");
            
            try {
                String[] coords = scanner.nextLine().trim().split(",");

                if (coords.length != 2) {
                    showErrorAndWait("Неверный формат. Введите две координаты через запятую");
                    continue;
                }

                double lat = Double.parseDouble(coords[0].trim());
                double lon = Double.parseDouble(coords[1].trim());

                if (lat < MIN_LAT || lat > MAX_LAT || lon < MIN_LON || lon > MAX_LON) {
                    showErrorAndWait(String.format(
                        "Координаты вне допустимого диапазона!\n" +
                        "Широта: от %.1f° до %.1f°\n" +
                        "Долгота: от %.1f° до %.1f°",
                        MIN_LAT, MAX_LAT, MIN_LON, MAX_LON
                    ));
                    continue;
                }

                return new double[]{lat, lon};
            } catch (NumberFormatException e) {
                showErrorAndWait("Ошибка в формате чисел. Используйте точку как разделитель дробной части");
            }
        }
    }

    private static File selectXmlFile() {
        clearConsole();
        System.out.println("=== Выбор файла карты ===");
        
        if (System.getProperty("java.awt.headless", "false").equals("false")) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Выберите XML файл карты OSM");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
                }
                public String getDescription() {
                    return "XML файлы карт OSM (*.xml)";
                }
            });
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".xml")) {
                    showErrorAndWait("Выбранный файл должен иметь расширение .xml");
                    return null;
                }
                return file;
            }
            showErrorAndWait("Файл не был выбран");
            return null;
        } else {
            System.out.print("Введите полный путь к XML файлу карты OSM: ");
            String filePath = scanner.nextLine().trim();
            File file = new File(filePath);
            if (!file.exists()) {
                showErrorAndWait("Указанный файл не существует");
                return null;
            }
            if (!file.getName().toLowerCase().endsWith(".xml")) {
                showErrorAndWait("Указанный файл должен иметь расширение .xml");
                return null;
            }
            return file;
        }
    }

    private static String getFileNameForType(String prefix, RoadsType type) {
        return prefix + "_" + (type == RoadsType.Vehicle ? "vehicle" : "pedestrian") + ".geojson";
    }

    private static void saveCurrentGraph() {
        if (!checkGraphLoaded())
            return;

        clearConsole();

        try {
            System.out.println("=== Сохранение графа ===");
            String graphFileName = getFileNameForType("graph", currentRoadsType);
            GeoJsonExporter.exportGraphToGeoJson(service.getGraph(), graphFileName);
            showErrorAndWait("Граф успешно сохранён в файл: " + graphFileName);
        } catch (Exception e) {
            showErrorAndWait("Ошибка при сохранении графа: " + e.getMessage());
        }
    }

    private static void loadGraphFromFile() {
        try {
            File file = selectXmlFile();

            if (file == null)
                return;

            currentFilePath = file.getAbsolutePath();
            currentRoadsType = selectRoadType(null);

            if (currentRoadsType != null) {
                clearConsole();
                System.out.println("=== Загрузка графа ===");
                System.out.println("Загружаем граф из файла...");
                service.loadGraph(new OSMDataReader(), currentRoadsType, new FileInputStream(file));
                showErrorAndWait("Граф успешно загружен");
            }
        } catch (Exception e) {
            showErrorAndWait("Ошибка при загрузке графа: " + e.getMessage());
        }
    }

    private static void reloadGraphWithDifferentType() {
        if (!checkGraphLoaded())
            return;

        try {
            String currentTypeStr = currentRoadsType == RoadsType.Vehicle ? "Автомобильные" : "Пешеходные";
            RoadsType newType = selectRoadType(currentTypeStr);

            if (newType != null && newType != currentRoadsType) {
                clearConsole();
                System.out.println("=== Обновление типа дорог ===");
                System.out.println("Перезагружаем граф с новым типом дорог...");
                currentRoadsType = newType;
                service.loadGraph(new OSMDataReader(), currentRoadsType, new FileInputStream(currentFilePath));
                showErrorAndWait("Граф успешно перезагружен с новым типом дорог");
            }
        } catch (Exception e) {
            showErrorAndWait("Ошибка при перезагрузке графа: " + e.getMessage());
        }
    }

    private static void findPath() {
        if (!checkGraphLoaded())
            return;

        try {
            clearConsole();
            System.out.println("=== Поиск пути ===");
            
            double[] startCoords = readCoordinates("начальные");
            double[] endCoords = readCoordinates("конечные");

            clearConsole();
            System.out.println("=== Поиск пути ===");
            System.out.println("Выполняется поиск пути...");
            var path = service.findPath(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);

            if (path.isEmpty()) {
                showErrorAndWait("Путь между указанными точками не найден");
            } else {
                clearConsole();
                System.out.println("\n=== Результат поиска ===");
                System.out.printf("Путь найден! Общая дистанция: %.2f км%n", path.getTotalDistance() * 100);
                
                String pathFileName = getFileNameForType("path", currentRoadsType);
                GeoJsonExporter.exportPathToGeoJson(path, pathFileName);
                System.out.println("\nПуть сохранён в файл: " + pathFileName);
                showErrorAndWait("Для просмотра пути загрузите файл на сайт geojson.io");
            }
        } catch (Exception e) {
            showErrorAndWait("Ошибка при поиске пути: " + e.getMessage());
        }
    }
}