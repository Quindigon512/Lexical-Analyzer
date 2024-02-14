import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Test {
    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 8; i++) {

            try {
                Reader solution = new FileReader("testsolu" + i + ".txt");
                Reader test = new FileReader("test" + i + ".txt");
                char[] solutionarr = new char[100000];
                char[] testarr = new char[100000];
                solution.read(solutionarr);
                test.read(testarr);

                int column = 0;
                int line = 0;
                boolean same = true;
                for (int j = 0; j < 100000; j++) {
                    if (solutionarr[j] == 0 || testarr[j] == 0) {
                        break;
                    }

                    if (solutionarr[j] != testarr[j]) {
                        same = false;
                        System.out.println("Error at: " + line + ":" + column + "solarr: " + solutionarr[j] + " testarr: " + testarr[j]);
                    }

                    if (solutionarr[j] == '\n') {
                        line++;
                    }

                    column++;
                }

                if (same) {
                    System.out.println("Case " + i + " success");
                }

            } catch (FileNotFoundException e) {
                throw e;
            }
        
            System.out.println();
        }
    }
}