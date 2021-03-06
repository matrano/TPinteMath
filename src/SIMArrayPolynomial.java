import javax.swing.JOptionPane;

/**
 * La classe <b>SIMArrayPolynomial</b> représente une application basée sur le projet d'intégration de <u>Samuel Langevin</u>
 * fondé sur la dérivée et l'intégrale d'un polynôme représenté dans un tableau de coefficient.
 *
 * @author Samuel Langevin et Simon Vezina
 * @since 2021-10-28
 * @version 2022-02-22
 */
public class SIMArrayPolynomial {

    /**
     * La constante <b>FUNCTION_ZERO</b> représente la fonction f(x) = 0.0 en chaîne de caractère correspondant à {@value}.
     */
    private static final String FUNCTION_ZERO = "0.0";

    /**
     *
     * Méthode main réaliser l'analyse d'un polynôme représenté dans un tableau de coefficients de valeurs.
     * @param args
     */
    public static void main(String[] args) {


        double[] array = readArrayPolynomial();
        double a = readDouble("Donnez la valeur de la borne inférieure (a) de l'intervalle d'intégration.");
        double b = readDouble("Donnez la valeur de la borne supérieure (b) de l'intervalle d'intégration.");

        // Affichage de la fonction, de son degré et de l'intervalle d'intégration de la fonction.
        System.out.println("f(x) = " + arrayPolynomialToString(array, 'x'));
        System.out.println("Le degré est " + degrePol(array));
        System.out.println("Intervalle d'intégration [a, b] : [" + a + ", " + b +"]" );


        // �crivez le restant du programme afin d'atteindre les objectifs de ce travail.

        System.out.println("La primitive de la fonction polynomiale avec pour C 0 est F(x) = "+ arrayPolynomialToString(primitive0(array), 'x'));
        System.out.println("L'integrale definie de "+ a + " a "+ b + " vaut " + computeDefiniteIntegral(array, a, b));
        System.out.println(isAreaQuad(array, a, b));



    }




    /**
     * Méthode pour faire la lecture d'un nombre de type double.
     *
     * @param message Le message qui sera affiché pour guider l'utilisateur de la méthode lors de l'exécution de cette méthode.
     * @return Le nombre de type double lu.
     */
    public static double readDouble(String message) {

        boolean succes;
        double value = 0.0;

        //-------------------------
        // Lecture du nombre double
        //-------------------------
        succes = false;

        do {
            try {

                value = Double.parseDouble(JOptionPane.showInputDialog(message));
                succes = true;

            }catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Le format de l'entrée n'est pas de type double. Recommencez!");
            }
        } while (!succes);


        return value;
    }





    /**
     * Méthode pour réaliser la lecture du polynôme à partir du clavier.
     *
     * @return Le polynôme.
     */
    public static double[] readArrayPolynomial() {

        double[] array = null;
        boolean succes;

        //------------------------------------------
        // Lecture de la taille maximale du polynôme.
        //-------------------------------------------
        succes = false;

        do {
            try {

                int size = Integer.parseInt(JOptionPane.showInputDialog("Indentifiez la puissance maximale de votre polyn�me"));

                // Validation de la taille qui doit �tre positif.
                if(size < 0)
                    throw new NumberFormatException();

                // Allocation de la taille du tableau.
                array = new double[size+1];
                succes = true;

            }catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Le format de l'entrée de la puissance maximale n'est pas valide. Recommencez!");
            }
        } while (!succes);



        //--------------------------
        // Lecture des coefficients.
        //--------------------------
        for(int i = 0; i < array.length; i++) {

            succes = false;

            do {
                try {

                    String expression = JOptionPane.showInputDialog("Indentifiez le coefficient de votre polynôme de puissance " + i);

                    if(expression.equals(""))
                        array[i] = 0.0;
                    else
                        array[i] = Double.parseDouble(expression);

                    succes = true;

                }catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Le format de l'entrée de votre coefficient n'est pas valide. Recommencez!");
                }
            } while (!succes);

        }

        return array;
    }





    /**
     * Méthode pour convertir en String l'expression d'un polynôme représenté dans un tableau
     * dans un ordre croissant de puissance.
     *
     * @param array Le tableau des coefficients.
     * @param x Le caractère de la variable.
     * @return Le String représentant le polynôme.
     */
    public static String arrayPolynomialToString(double[] array, char x) {

        // Redimensionner le tableau en retirant les coefficients 0.0 pour la puissance la plus élevée.
        double[] resized_array = resizeArrayPolynomial(array);

        // Accumulateur des caractères.
        StringBuffer buffer = new StringBuffer("");

        // Gestion des autres termes à afficher.
        for(int i = resized_array.length-1; i > -1; i--) {

            // Affichage du terme en puissance si le coefficient n'est pas 0.0.
            if(resized_array[i] != 0.0) {

                // Mettre le signe.
                if(resized_array[i] < 0)
                    buffer.append(" - ");
                else
                if(i != (resized_array.length-1))
                    buffer.append(" + ");

                // Mettre le coefficient en valeur absolue, car le signe y est déjà.
                // Cependant, nous n'allons pas afficher le terme "1.0" sauf pour la puissance 0.
                if(Math.abs(resized_array[i]) != 1.0 || i == 0)
                    buffer.append(Math.abs(resized_array[i]));

                // Ajouter la variable x si le degré n'est pas zéro.
                if(i != 0) {

                    // Mettre la variable x du polynôme.
                    buffer.append(x);

                    // Mettre l'exposant au polynôme s'il est supérieur à 1.
                    if(i > 1) {
                        buffer.append("^");
                        buffer.append(i);
                    }
                }
            }

        }

        // Expression du polynôme en String.
        String expression = buffer.toString();

        // Cas particulier : Le polynôme f(x) = 0.0.
        if(expression.equals(""))
            expression = FUNCTION_ZERO;

        // Cas général, l'expression est valide.
        return expression;
    }






    /**
     * Méthode permettant de redimensionner le tableau de coefficients représentant un polynôme dont les puissances sont en ordre croissant
     * afin de réduire la taille du tableau si les coefficients des puissances les plus élevées du polynôme sont nuls.
     *
     * @param array Le tableau des coefficients du polynôme
     * @return Le tabeau des coefficients du polynôme redimensionné.
     */
    public static double[] resizeArrayPolynomial(double[] array) {

        // Dénombrer le nombre de terme "0.0" à retirer du polynôme.
        int count  = 0;

        for(int i = array.length-1; i > -1; i--) {

            if(array[i] == 0.0)
                count++;
            else
                break;
        }

        // Cas particulier : Il n'y a pas de redimension à réaliser, donc on peut retourner la réponse.
        if(count == 0)
            return array;

        // Allocation de la mémoire au nouveau tableau redimensionné.
        double[] resized_array = new double[array.length - count];

        // Cas particulier : Si la fonction est f(x) = 0.0, alors la nouvelle taille du tableau serait zéro ce qui n'est pas valide.
        if(resized_array.length == 0)
            return new double[1];

        // Remplir le nouveau tableau (version rapide).
        System.arraycopy(array, 0, resized_array, 0, resized_array.length);

        return resized_array;
    }





    /**
     * Méthode pour évaluer un polynôme f(x) représenté en tableau de coefficients à une coordonnée x donnée.
     *
     * @param array Le tableau des coefficients.
     * @param x La coordonnée x où la fonction est évaluée.
     * @return La valeur de la fonction.
     */
    public static double evaluatePolynomial(double[] array, double x) {

        //Initialisation
        double value = 0.0;

        //On additionne ensemble les évaluations pour chaque terme.
        for(int i = 0; i < array.length; i++)
            value += array[i] * Math.pow(x, (double) i);

        return value;
    }






    /**
     * Méthode pour déterminer le degré d'un polynôme f(x).
     * Le résultat retourné sera un entier.
     *
     * @param array Le tableau des coefficients.
     * @return Le degré du polynôme.
     */
    public static int degrePol(double[] array) {


        int k=array.length-1;
        int degre=array.length-1;

        //On teste les coefficients en partant des puissances les plus élevées.
        //On trouve ainsi le premier terme non nul qui est associé au degré du polynôme.
        while(array[k]==0 && k!=0) {
            degre=k-1;
            k--;
        }

        return degre;
    }







    /**
     * Méthode pour déterminer le nombre de zéros d'un polynôme de degré 2 f(x).
     * Le résultat retourné sera un entier.
     *
     * @param array Le tableau des coefficients.
     * @return Le nombre de zéros du polynôme de degré 2.
     */
    public static int numberZeroQuad(double[] array) {

        //Initialisation
        int nbZero;

        //On vérifie que le polynôme est bien de degré 2. Sinon, le programme retourne 99 comme nombre de zéros.
        if(degrePol(array)==2) {

            //Dans la formule quadratique, le discriminant est la valeur de l'expression sous la racine carrée.
            //Le signe du discriminant nous indique le nombre de zéros du polynôme quadratique.
            double discriminant=Math.pow(array[1],2)-4*array[2]*array[0];

            if(discriminant>0) {
                nbZero = 2;

            } else if (discriminant==0) {
                nbZero = 1;

            } else {
                nbZero = 0;
            }

        }else {
            nbZero=99;
        }

        return nbZero;

    }




    /**
     * Méthode pour déterminer la valeur des zéros d'un polynôme f(x) de degré 2.
     * La méthode retourne un tableau contenant les zéros réels du polynôme, en ordre croissant.
     *
     * @param array Le tableau des coefficients.
     * @return Le tableau des zéros du polynôme.
     */
    public static double[] valueZeroQuad(double[] array) {


        //On initialise un tableau qui contiendra la valeur des zéros.
        double[] zeroTab=null;


        //On vérifie que le polynôme est bien de degré 2. Sinon, le programme s'arrête.
        if(degrePol(array)==2) {

            // La taille du tableau dépend du nombre de zéros du polynôme quadratique.
            int nbZero=numberZeroQuad(array);
            zeroTab= new double[nbZero];

            //Discriminant pour la formule quadratique.
            double discriminant=Math.pow(array[1],2)-4*array[2]*array[0];
            double temp = 0.0;

            // La valeur des zéros est donnée par la formule quadratique.
            if(nbZero==2) {
                zeroTab[0] = (-array[1]+Math.pow(discriminant,0.5))/(2*array[2]);
                zeroTab[1] = (-array[1]-Math.pow(discriminant,0.5))/(2*array[2]);

                //On s'assure que les 2 zéros sont en ordre croissant.
                if(zeroTab[0]>zeroTab[1]) {
                    temp=zeroTab[0];
                    zeroTab[0]=zeroTab[1];
                    zeroTab[1]=temp;
                }

                //S'il n'y a qu'un seul zéro, la formule quadratique est plus simple.
            }else if(nbZero==1) {
                zeroTab[0]=-array[1]/(2*array[2]);
            }

            //S'il n'y a pas de zéro, le tableau est de taille 0 et il n'y a rien à mettre à l'intérieur.

            //S'il y a lieu, on remplace la valeur -0.0 par la valeur 0.0.
            for(int i=0;i<zeroTab.length;i++) {
                if(zeroTab[i]==-0.0) {
                    zeroTab[i]=0.0;
                }
            }
        }


        return zeroTab;
    }





    /**
     * Méthode réalisant une intégrale indéfinie (ayant une constante=0) d'un tableau de coefficients représentant un polynôme dont les puissances sont en ordre croissant.
     *
     * @param array Le tableau des coefficients.
     * @return Le tableau des coefficients de la fonction primitive dont la constante est 0.
     */
    public static double[] primitive0(double[] array) {
        double[] primitive0Array = new double[array.length+1];
        primitive0Array[0] = 0; // La constance est 0
        for (int i = 0; i < array.length; i++) {
            primitive0Array[i+1] = array[i]/(i+1); // On transforme à la suite chacun des coefficient en les combinant avec les coefficients fourni par la formule d'intégration des puissances de x
        }
        return primitive0Array;
    }





    /**
     * Méthode pour évaluer l'intégrale définie d'un polynôme f(x) entre x=a et x=b.
     * Si la fonction f est positive ou nulle sur l'intervalle, la valeur calculée représente l'aire entre la courbe de f et l'axe des x.
     *
     * @param array Le tableau des coefficient.
     * @param a La borne inférieure d'intégration.
     * @param b La borne supérieure d'intégration.
     * @return L'intégrale définie de la fonction.
     */
    public static double computeDefiniteIntegral(double[] array, double a, double b) {
        double[] primitive0Array = primitive0(array);
        return computePolynomial(primitive0Array, b) - computePolynomial(primitive0Array, a); // Simple application du théorème fondamental du calcul intégrale
    }






    /**
     * Méthode pour déterminer le nombre de zéros d'un polynôme de degré 2 f(x).
     * Le résultat retourné sera un entier.
     *
     * @param array Le tableau des coefficient.
     * @param a La borne inférieure d'intégration.
     * @param b La borne supérieure d'intégration.
     * @return Le nombre de zéros du polynôme de degré 2.
     */
    public static String isAreaQuad(double[] array, double a, double b) {
        if (array.length != 3) {
            System.out.println("La méthode isAreaQuad() ne permet pas de polynôme dont le degrée est different de 2");
            return "La méthode isAreaQuad() ne permet pas de polynôme dont le degrée est différent de 2";
        }
        if (a > b) {
            System.out.println("La méthode isAreaQuad() ne permet pas que la borne inférieur soit plus grande que la borne supérieur");
            return "La méthode isAreaQuad() ne permet pas que la borne inférieur soit plus grande que la borne supérieur";
        }
        boolean isOnTopX;

        if (array[2] > 0) { //Parabole ouverte vers le haut
            isOnTopX = numberZeroQuad(array) <= 0; // il y a un ou aucun zero
            if (!isOnTopX) // Il y a 2 zero
                isOnTopX = (  a <= valueZeroQuad(array)[0] && b <= valueZeroQuad(array)[0] || // Si les bornes sont avant ou apres chacun des zeros
                        a >= valueZeroQuad(array)[1] && b >= valueZeroQuad(array)[1]);
        } else {
            isOnTopX = !(numberZeroQuad(array) <= 0); // La courbe est constament sous l'axe des x
            if (!isOnTopX)
                isOnTopX = (a >= valueZeroQuad(array)[0] && b<=valueZeroQuad(array)[1]);
        };
        if (a<0) isOnTopX = false; //Si la borne inférieur est derrière l'axe des y, l'aire ne peut pas correspond au résultat donne par l'intégrale définie
        return "L'intégrale définie de a à b " +(isOnTopX?"":"ne ") +"correspond "+ (isOnTopX?"":"pas ")+"a l'aire sous la courbe";
    }

    /**
     * Calcule le resultat d'un polynôme evaluer en x
     * @param array Le tableau des coefficient.
     * @param x La valeur de x
     * @return une valeur entre -infinie et +infinie
     */
    private static double computePolynomial(double[] array, double x) {
        double result = 0.0D;
        for (int i = 0; i < array.length; i++)
            result += array[i] * Math.pow(x,i);
        return result;
    }
}
