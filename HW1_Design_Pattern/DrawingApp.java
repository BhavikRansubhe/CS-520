package CS520.HW1_Design_Pattern;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

// Command interface
interface Command {
    void execute();
    void undo();
}

// Receiver interface
interface Shape {
    void move(int dx, int dy);
    void setColor(Color color);
    void draw(Graphics g);
    boolean contains(int x, int y);
    int getX();
    int getY();
}


// Concrete Command
class MoveCommand implements Command {
    private Shape shape;
    private int dx, dy;

    public MoveCommand(Shape shape, int dx, int dy) {
        this.shape = shape;
        this.dx = dx;
        this.dy = dy;
    }

    public void execute() {
        shape.move(dx, dy);
    }

    public void undo() {
        shape.move(-dx, -dy);
    }
}

// Concrete Receiver: Circle
class Circle implements Shape {
    private int x, y, radius;
    private Color color;

    public Circle(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public boolean contains(int x, int y) {
        int dx = this.x - x;
        int dy = this.y - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}

// Concrete Receiver: Square
class Square implements Shape {
    private int x, y, side;
    private Color color;

    public Square(int x, int y, int side, Color color) {
        this.x = x;
        this.y = y;
        this.side = side;
        this.color = color;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, side, side);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + side && y >= this.y && y <= this.y + side;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}

// Invoker
class DrawingCanvas extends JPanel {
    private ArrayList<Shape> shapes = new ArrayList<>();
    private Stack<Command> undoStack = new Stack<>();
    private Shape selectedShape = null;
    private Color selectedColor = Color.BLACK;

    public DrawingCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = shapes.size() - 1; i >= 0; i--) {
                    Shape shape = shapes.get(i);
                    if (shape.contains(e.getX(), e.getY())) {
                        selectedShape = shape;
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (selectedShape != null) {
                    int endX = e.getX();
                    int endY = e.getY();
                    int dx = endX - selectedShape.getX();
                    int dy = endY - selectedShape.getY();

                    if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
                        Command command = new MoveCommand(selectedShape, dx, dy);
                        command.execute();
                        undoStack.push(command);
                        repaint();
                    }
                }
            }
        });
    }

    public void changeColor(Color color) {
        selectedColor = color;
        if (selectedShape != null) {
            selectedShape.setColor(color);
            repaint();
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    public void setSelectedShape(Shape shape) {
        selectedShape = shape;
        selectedShape.setColor(selectedColor);
        repaint();
    }
}

public class DrawingApp extends JFrame {
    private DrawingCanvas canvas;

    public DrawingApp() {
        setTitle("Drawing Application");
        canvas = new DrawingCanvas();
        getContentPane().add(canvas, BorderLayout.CENTER);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> canvas.undo());

        JButton circleButton = new JButton("Circle");
        circleButton.addActionListener(e -> {
            Circle circle = new Circle(100, 100, 30, Color.BLACK);
            canvas.addShape(circle);
            canvas.setSelectedShape(circle);
        });

        JButton squareButton = new JButton("Square");
        squareButton.addActionListener(e -> {
            Square square = new Square(100, 100, 50, Color.BLACK);
            canvas.addShape(square);
            canvas.setSelectedShape(square);
        });

        JButton colorButton = new JButton("Change Color");
        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a Color", Color.BLACK);
            if (color != null) {
                canvas.changeColor(color);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(undoButton);
        buttonPanel.add(circleButton);
        buttonPanel.add(squareButton);
        buttonPanel.add(colorButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingApp());
    }
}
