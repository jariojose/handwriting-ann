import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.JPanel;

public class NeuralNetwork extends JPanel implements Serializable  {

	Neuron[][] neurons;

	// learning constant
	double LEARNING_CONSTANT = (double) 0.2;

	public NeuralNetwork(int layers, int neuronsPerLayer, int inputs,
			int outputs) {
		// init number of neurons according to the number of layers and neurons
		// per layer
		// Neuron array is held by neuron[row][column]
		neurons = new Neuron[layers][];
		// we need to init all the neurons in these layers
		for (int i = 0; i < layers; i++) {
			if (i == 0) {
				neurons[i] = new Neuron[inputs];
				for (int k = 0; k < inputs; k++) {
					neurons[i][k] = new Neuron(inputs, true);
				}
			} else if (i == 1) {
				neurons[i] = new Neuron[neuronsPerLayer];
				for (int k = 0; k < neuronsPerLayer; k++) {
					neurons[i][k] = new Neuron(inputs);
				}
			} else if (i == layers - 1) {
				neurons[i] = new Neuron[outputs];
				for (int k = 0; k < outputs; k++) {
					neurons[i][k] = new Neuron(neuronsPerLayer);
				}
			} else {
				neurons[i] = new Neuron[neuronsPerLayer];
				for (int k = 0; k < neuronsPerLayer; k++) {
					neurons[i][k] = new Neuron(neuronsPerLayer);
				}
			}

		}
		// System.out.println("done init");
	}

	protected void paintComponent(Graphics g) {

		g.setColor(Color.RED);
		g.drawLine(0, 0, 20, 20);

		int xFlat = 100;
		int yFlat = 100;
		int xOffset = 120;
		int yOffset = 120;
		int width = 80;
		int height = 80;
		for (int i = 0; i < neurons.length; i++) {
			for (int k = 0; k < neurons[i].length; k++) {
				g.drawRect(xFlat + xOffset * i, yFlat + yOffset * k, width,
						height);
				for (int j = 0; j < neurons[i][k].weights.length; j++) {
					g.drawString(neurons[i][k].weights[j] + " ", xFlat
							+ xOffset * i, yOffset * (k + j));
				}
			}
		}
	}

	public double[] getLayerOutput(double[] input, int layer) throws Exception {
		double[] output = new double[neurons[layer].length];
		// System.out.println(neurons[layer].length);
		// System.out.println(output.length);

		// System.out.println("LAYER CALLED");
		for (int i = 0; i < neurons[layer].length; i++) {
			output[i] = neurons[layer][i].output(input);
			// System.out.print(output[i] + ", ");
		}
		// System.out.println();
		return output;
	}

	

	
	public double[] getResult(double input) throws Exception {

		return getResult(new double[] { input });

	}

	public double[] getResult(double[] input) throws Exception {
		// first run the inputs through the first layer
		int layers = neurons.length;
		double[] currInput = input;
		for (int i = 0; i < layers; i++) {
			// for each layer
			currInput = getLayerOutput(currInput, i);
		}
		return currInput;
	}

	
	public void train(double input, double target){
		try{
			train(Utils.toDoubleArray(input),Utils.toDoubleArray(target));
		}
		catch(Exception e){
			//fail silently 
		}
	}
	
	
	public void train(double[] input, double[] target) throws Exception {
		// first run the inputs through the first layer
		double[] actual = getResult(input);
		double[] error = new double[actual.length];
		// System.out.println(actual[0]);

		// calculate error for each output node
		for (int i = 0; i < actual.length; i++) {
			error[i] = actual[i] * (1 - actual[i]) * (target[i] - actual[i]) ;
			//System.out.println("output error : " + error[i]);
			// System.out.println(error[i]);
			neurons[neurons.length - 1][i].currDelta = error[i];
		
		}


		for (int i = neurons.length - 2; i >= 0; i--) {
			// System.out.println("layer " + i);
			// calculate error for the next layer
			double[] nextError = new double[neurons[i].length];
			for (int j = 0; j < neurons[i].length; j++) {
				nextError[j] = neurons[i][j].getDelta(error,neurons[i + 1], j);
			}
			error = nextError;
		}


		for (int i = neurons.length - 1; i >= 0; i--) {
			//System.out.println(i);
			//System.out.println("layer " + (i + 1));
			for (int j = 0; j < neurons[i].length; j++) {
				//System.out.println("\t N " + j);
				neurons[i][j].adjustWeights(LEARNING_CONSTANT);
				for(int k = 0; k<neurons[i][j].weights.length; k++){
					//System.out.println("\t \t" + neurons[i][j].weights[k] + "    " + neurons[i][j].deltaWeights[k]);
					//System.out.println("\t \t bias : " + neurons[i][j].bias + "    " + neurons[i][j].biasChange);
				}
			}
		}

	}
}