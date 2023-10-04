import java.util.*;

// Interface pour les générateurs de labyrinthes
interface MazeGenerator {
    char[][] generate(int width, int height);
}

// Classe Maze pour représenter le labyrinthe
class Maze {
    private char[][] maze;

    public Maze(char[][] maze) {
        this.maze = maze;
    }

    public void display() {
        for (char[] row : maze) {
            for (char cell : row) {
                System.out.print(cell);
            }
            System.out.println("#");
        }
    }
}

// Générateur de labyrinthes imparfaits simples
class SimpleImperfectMazeGenerator implements MazeGenerator {
    @Override
    public char[][] generate(int width, int height) {
        char[][] maze = new char[height][width];
        Random rand = new Random();

        // Remplir le labyrinthe avec des murs
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = '#'; // '#' représente un mur
            }
        }

        // Creuser des passages aléatoirement
        for (int i = 1; i < height - 1; i += 2) {
            for (int j = 1; j < width - 1; j += 2) {
                maze[i][j] = '.'; // Creuser un passage

                // Creuser un passage supplémentaire (imperfection)
                if (rand.nextDouble() < 0.5) {
                    int dir = rand.nextInt(4);
                    switch (dir) {
                        case 0: // Haut
                            if (i > 1) maze[i - 1][j] = '.';
                            break;
                        case 1: // Droite
                            if (j < width - 2) maze[i][j + 1] = '.';
                            break;
                        case 2: // Bas
                            if (i < height - 2) maze[i + 1][j] = '.';
                            break;
                        case 3: // Gauche
                            if (j > 1) maze[i][j - 1] = '.';
                            break;
                    }
                }
            }
        }

        return maze;
    }
}

// Générateur de labyrinthes parfaits simples
class SimplePerfectMazeGenerator implements MazeGenerator {
    @Override
    public char[][] generate(int width, int height) {
        char[][] maze = new char[height][width];
        Random rand = new Random();

        // Remplir le labyrinthe avec des murs
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = '#'; // '#' représente un mur
            }
        }

        // Choisir une cellule de départ (au hasard)
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);

        // Marquer la cellule de départ comme faisant partie du labyrinthe
        maze[startY][startX] = ' ';

        // Liste des murs à explorer
        List<int[]> walls = new ArrayList<>();
        walls.add(new int[]{startX, startY});

        while (!walls.isEmpty()) {
            // Choisir un mur aléatoire de la liste
            int[] currentWall = walls.remove(rand.nextInt(walls.size()));
            int x = currentWall[0];
            int y = currentWall[1];

            // Vérifier les voisins
            int[][] neighbors = {
                {x - 2, y}, {x + 2, y}, {x, y - 2}, {x, y + 2}
            };

            Collections.shuffle(Arrays.asList(neighbors));

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                // Vérifier si le voisin est valide
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (maze[ny][nx] == '#') {
                        // Retirer le mur entre la cellule actuelle et le voisin
                        maze[ny][nx] = ' ';
                        maze[y + (ny - y) / 2][x + (nx - x) / 2] = ' ';

                        // Ajouter le voisin à la liste des murs à explorer
                        walls.add(new int[]{nx, ny});
                    }
                }
            }
        }

        return maze;
    }
}

// Générateur de labyrinthes basés sur un graphe
class GraphBasedMazeGenerator implements MazeGenerator {
    @Override
    public char[][] generate(int width, int height) {
        char[][] maze = new char[height][width];
        Random rand = new Random();

        // Remplir le labyrinthe avec des murs
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = '#'; // '#' représente un mur
            }
        }

        // Créer un graphe de cellules
        List<int[]> cells = new ArrayList<>();
        for (int i = 1; i < height - 1; i += 2) {
            for (int j = 1; j < width - 1; j += 2) {
                cells.add(new int[]{i, j});
            }
        }

        // Choisir une cellule de départ (au hasard)
        int[] startCell = cells.get(rand.nextInt(cells.size()));
        int startX = startCell[1];
        int startY = startCell[0];

        // Marquer la cellule de départ comme faisant partie du labyrinthe
        maze[startY][startX] = ' ';

        // Créer un ensemble de cellules visitées
        Set<int[]> visitedCells = new HashSet<>();
        visitedCells.add(startCell);

        while (!cells.isEmpty()) {
            // Choisir une cellule au hasard parmi les cellules visitées
            int[] currentCell = visitedCells.stream().skip(rand.nextInt(visitedCells.size())).findFirst().get();
            int x = currentCell[1];
            int y = currentCell[0];

            // Liste des voisins non visités
            List<int[]> unvisitedNeighbors = new ArrayList<>();

            int[][] neighbors = {
                {x - 2, y}, {x + 2, y}, {x, y - 2}, {x, y + 2}
            };

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                // Vérifier si le voisin est valide et non visité
                if (nx >= 1 && nx < width - 1 && ny >= 1 && ny < height - 1) {
                    int[] neighborCell = {ny, nx};
                    if (!visitedCells.contains(neighborCell)) {
                        unvisitedNeighbors.add(neighborCell);
                    }
                }
            }

            if (!unvisitedNeighbors.isEmpty()) {
                // Choisir un voisin au hasard
                int[] neighborCell = unvisitedNeighbors.get(rand.nextInt(unvisitedNeighbors.size()));
                int nx = neighborCell[1];
                int ny = neighborCell[0];

                // Retirer le mur entre la cellule actuelle et le voisin
                maze[ny][nx] = ' ';
                maze[y + (ny - y) / 2][x + (nx - x) / 2] = ' ';

                // Marquer le voisin comme visité
                visitedCells.add(neighborCell);

                // Ajouter le voisin à la liste des cellules à explorer
                cells.add(neighborCell);
            } else {
                // Supprimer la cellule actuelle de la liste des cellules à explorer
                cells.remove(currentCell);
            }
        }

        return maze;
    }
}

// Générateur de labyrinthes optimisés
class OptimizedMazeGenerator implements MazeGenerator {
    @Override
    public char[][] generate(int width, int height) {
        char[][] maze = new char[height][width];
        Random rand = new Random();

        // Initialisation : remplir tout le labyrinthe avec des murs
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = '#'; // '#' représente un mur
            }
        }

        // Sélectionner une cellule de départ (au hasard)
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);

        // Marquer la cellule de départ comme faisant partie du labyrinthe
        maze[startY][startX] = ' ';

        // Liste des murs à explorer
        List<int[]> walls = new ArrayList<>();
        walls.add(new int[]{startX, startY});

        while (!walls.isEmpty()) {
            // Choisir un mur aléatoire de la liste
            int[] currentWall = walls.remove(rand.nextInt(walls.size()));
            int x = currentWall[0];
            int y = currentWall[1];

            // Vérifier les voisins
            int[][] neighbors = {
                {x - 2, y}, {x + 2, y}, {x, y - 2}, {x, y + 2}
            };

            Collections.shuffle(Arrays.asList(neighbors));

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                // Vérifier si le voisin est valide
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (maze[ny][nx] == '#') {
                        // Creuser un passage vers le voisin
                        maze[ny][nx] = ' ';
                        maze[y + (ny - y) / 2][x + (nx - x) / 2] = ' ';

                        // Ajouter le voisin à la liste des murs à explorer
                        walls.add(new int[]{nx, ny});
                    }
                }
            }
        }

        return maze;
    }
}

// Classe principale du programme
public class MazeRunner {
    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                throw new IllegalArgumentException("Erreur : Veuillez fournir une largeur et une hauteur valides supérieures à 5, ainsi qu'un type de labyrinthe et une méthode de génération valides.");
            }

            int width = Integer.parseInt(args[0]);
            int height = Integer.parseInt(args[1]);
            String mazeType = args[2];
            String generationMethod = args[3];

            MazeGenerator mazeGenerator = null;

            if (mazeType.equalsIgnoreCase("imperfect")) {
                mazeGenerator = new SimpleImperfectMazeGenerator();
            } else if (mazeType.equalsIgnoreCase("perfect")) {
                mazeGenerator = new SimplePerfectMazeGenerator();
            } else if (mazeType.equalsIgnoreCase("graph")) {
                mazeGenerator = new GraphBasedMazeGenerator();
            } else if (mazeType.equalsIgnoreCase("optimized")) {
                mazeGenerator = new OptimizedMazeGenerator();
            } else {
                throw new IllegalArgumentException("Erreur : Type de labyrinthe non valide.");
            }

            char[][] maze = mazeGenerator.generate(width, height);
            Maze mazeObj = new Maze(maze);
            mazeObj.display();
        } catch (NumberFormatException e) {
            System.err.println("Erreur : Veuillez fournir une largeur et une hauteur valides supérieures à 5.");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la génération du labyrinthe. Veuillez réessayer.");
        }
    }
}
