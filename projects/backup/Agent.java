package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */

	public Agent() {
	}

	public int Solve(RavensProblem problem) {

		RavensFigure A = problem.getFigures().get("A");
		RavensFigure B = problem.getFigures().get("B");
		RavensFigure C = problem.getFigures().get("C");
		RavensFigure f1 = problem.getFigures().get("1");
		RavensFigure f2 = problem.getFigures().get("2");
		RavensFigure f3 = problem.getFigures().get("3");
		RavensFigure f4 = problem.getFigures().get("4");
		RavensFigure f5 = problem.getFigures().get("5");
		RavensFigure f6 = problem.getFigures().get("6");
		RavensFigure[] answers = new RavensFigure[] { f1, f2, f3, f4, f5, f6 };

		int result = 6;

		ArrayList<Transformation> targetTransformsAB = computeTransformation(A,
				B);
		ArrayList<Transformation> targetTransformsAC = computeTransformation(A,
				C);

		int[] diffScoresAB = new int[6];
		int[] diffScoresAC = new int[6];

		System.out.println("New Problem:");
		System.out.println();
		System.out.println("A--->B mutations:");
		for (Transformation t : targetTransformsAB) {
			System.out.println(t.getMutations());
		}
		for (int i = 0; i < 6; i++) {
			ArrayList<Transformation> choiceTransforms = computeTransformation(
					C, answers[i]);
			diffScoresAB[i] = computeDifference(targetTransformsAB,
					choiceTransforms);
			System.out.println(String.format("C ---> %d:", i + 1));
			System.out
					.println(String.format("diffScore = %d", diffScoresAB[i]));
			for (Transformation t : choiceTransforms) {
				System.out.println(String.format("mutations: %s",
						t.getMutations()));
			}
		}

		int lowestDiffScore = 999;
		for (int i = 0; i < diffScoresAB.length; i++) {
			if (diffScoresAB[i] < lowestDiffScore) {
				lowestDiffScore = diffScoresAB[i];
				result = i + 1;
			}
		}

		System.out.println();
		System.out.println("A--->C mutations:");
		for (Transformation t : targetTransformsAC) {
			System.out.println(t.getMutations());
		}
		for (int i = 0; i < 6; i++) {
			ArrayList<Transformation> choiceTransforms = computeTransformation(
					B, answers[i]);
			diffScoresAC[i] = computeDifference(targetTransformsAC,
					choiceTransforms);
			System.out.println(String.format("B ---> %d:", i + 1));
			System.out
					.println(String.format("diffScore = %d", diffScoresAC[i]));
			for (Transformation t : choiceTransforms) {
				System.out.println(String.format("mutations: %s",
						t.getMutations()));
			}
		}

		for (int i = 0; i < diffScoresAC.length; i++) {
			if (diffScoresAC[i] < lowestDiffScore) {
				lowestDiffScore = diffScoresAC[i];
				result = i + 1;
			}
		}

		System.out.println();
		System.out.println("Answer:");
		System.out.println(result);
		System.out
				.println("-----------------------------------------------------------------------");

		return result;

	}

	private int computeDifference(ArrayList<Transformation> targetTransforms,
			ArrayList<Transformation> choiceTransforms) {
		int result = 0;

		HashMap<Transformation, Transformation> mapTr = new HashMap<Transformation, Transformation>();
		for (Transformation target : targetTransforms) {
			mapTr.put(target, null);
		}

		HashMap<Transformation, Boolean> matchedTr = new HashMap<Transformation, Boolean>();
		for (Transformation choice : choiceTransforms) {
			matchedTr.put(choice, false);
		}

		for (Transformation target : targetTransforms) {
			int lowestScore = 9999;
			for (Transformation choice : choiceTransforms) {

				if (!matchedTr.get(choice)) {
					int diff = getDiffTr(target, choice);
					if (diff < lowestScore) {
						lowestScore = diff;
						mapTr.put(target, choice);
					}
				}
			}

			if (mapTr.get(target) != null) {
				matchedTr.put(mapTr.get(target), true);
				result += lowestScore;
			} else {
				result += 3; // Penalty score for not finding a matching object
			}

		}

		return result;
	}

	private int getDiffTr(Transformation target, Transformation choice) {
		int diff = 0;

		if (target.getMutations() == null && choice.getMutations() != null) {
			diff = 10;
			return diff;
		}

		if (target.getMutations() != null && choice.getMutations() == null) {
			diff = 10;
			return diff;
		}

		if (target.getMutations().size() != choice.getMutations().size()) {
			diff = 10;
			return diff;
		}

		for (String mut1 : target.getMutations()) {
			boolean found = false;

			for (String mut2 : choice.getMutations()) {
				if (mut1.compareTo(mut2) == 0) {
					found = true;
					break;
				}
			}

			if (!found) {
				diff++;
			}
		}

		return diff;
	}

	private ArrayList<Transformation> computeTransformation(RavensFigure A,
			RavensFigure B) {

		ArrayList<Transformation> results = new ArrayList<Transformation>();

		HashMap<RavensObject, RavensObject> map = new HashMap<RavensObject, RavensObject>();
		for (Map.Entry<String, RavensObject> entry1 : A.getObjects().entrySet()) {
			map.put(entry1.getValue(), null);
		}

		HashMap<RavensObject, Boolean> matched = new HashMap<RavensObject, Boolean>();
		for (Map.Entry<String, RavensObject> entry2 : B.getObjects().entrySet()) {
			matched.put(entry2.getValue(), false);
		}

		for (Map.Entry<String, RavensObject> entry1 : A.getObjects().entrySet()) {
			RavensObject ob1 = entry1.getValue();
			int lowest_diff = 2;

			for (Map.Entry<String, RavensObject> entry2 : B.getObjects()
					.entrySet()) {
				RavensObject ob2 = entry2.getValue();
				
				if(A.getObjects().size()==1 && B.getObjects().size() == 1){
					map.put(ob1, ob2);
				}

				if (!matched.get(ob2)) {
					int diff = getDiff(ob1, ob2);
					if (diff < lowest_diff) {
						lowest_diff = diff;
						map.put(ob1, ob2);
					}
				}
			}

			if (map.get(ob1) != null) {
				matched.put(map.get(ob1), true);
			}
		}

		for (RavensObject ob1 : map.keySet()) {

			if (map.get(ob1) == null) {
				Transformation tr = new Transformation();
				tr.setSource(ob1);
				tr.setTarget(null);
				ArrayList<String> mutations = new ArrayList<String>();
				mutations.add(String.format("%s disappeared", ob1
						.getAttributes().get("shape")));
				tr.setMutations(mutations);
				results.add(tr);
			} else {
				RavensObject ob2 = map.get(ob1);
				HashMap<String, String> ob1Attrs = ob1.getAttributes();
				HashMap<String, String> ob2Attrs = ob2.getAttributes();
				Transformation tr = new Transformation();
				tr.setSource(ob1);
				tr.setTarget(ob2);
				ArrayList<String> mutations = new ArrayList<String>();

				if (ob1Attrs.get("shape").compareTo(ob2Attrs.get("shape")) != 0) {
					mutations.add(String.format("reshaped from %s to %s",
							ob1Attrs.get("shape"), ob2Attrs.get("shape")));
				}

				HashMap<String, Integer> sizeMap = new HashMap<>();
				sizeMap.put("huge", 6);
				sizeMap.put("very large", 5);
				sizeMap.put("large", 4);
				sizeMap.put("medium", 3);
				sizeMap.put("small", 2);
				sizeMap.put("very small", 1);
				if (sizeMap.get(ob1Attrs.get("size")) > sizeMap.get(ob2Attrs
						.get("size"))) {
					mutations.add("expanded");
				}
				if (sizeMap.get(ob1Attrs.get("size")) < sizeMap.get(ob2Attrs
						.get("size"))) {
					mutations.add("shrunk");
				}

				if (ob1Attrs.get("fill").compareTo(ob2Attrs.get("fill")) != 0) {
					mutations.add(String.format("filled changed from %s to %s",
							ob1Attrs.get("fill"), ob2Attrs.get("fill")));
				}

				if (ob1Attrs.get("angle") != null
						&& ob2Attrs.get("angle") != null
						&& ob1Attrs.get("angle").compareTo(
								ob2Attrs.get("angle")) != 0) {
					if ((Integer.parseInt(ob1Attrs.get("angle")) + Integer
							.parseInt(ob2Attrs.get("angle"))) % 180 == 0
							&& (Integer.parseInt(ob1Attrs.get("angle")) + Integer
									.parseInt(ob2Attrs.get("angle"))) % 360 != 0) {
						mutations.add("Vertical Mirroring");
					} else if ((Integer.parseInt(ob1Attrs.get("angle")) + Integer
							.parseInt(ob2Attrs.get("angle"))) % 360 == 0) {
						mutations.add("Horizontal Mirroring");
					} else {
						int angleDiff = (Integer
								.parseInt(ob1Attrs.get("angle"))
								- Integer.parseInt(ob2Attrs.get("angle")) + 360) % 360;
						mutations.add(String.format("rotated %d", angleDiff));
					}
				}
				
				
				HashMap<String, String> mirrorAlignments = new HashMap<String, String>();
				mirrorAlignments.put("top-left", "top-right");
				mirrorAlignments.put("top-right", "top-left");
				mirrorAlignments.put("bottom-left", "bottom-right");
				mirrorAlignments.put("bottom-right", "bottom-left");
				
				if (ob1Attrs.get("alignment") != null
						&& ob2Attrs.get("alignment") != null
						&& ob1Attrs.get("alignment").compareTo(
								ob2Attrs.get("alignment")) != 0) {
					if(mirrorAlignments.get(ob1Attrs.get("alignment")).compareTo(
							ob2Attrs.get("alignment")) == 0){
						mutations.add("mirrored alignment");
					}else{
					mutations.add(String.format(
							"alignment changed from %s to %s",
							ob1Attrs.get("alignment"),
							ob2Attrs.get("alignment")));
					}
				}

				if (ob1Attrs.get("inside") != null) {
					if (ob2Attrs.get("inside") == null) {
						mutations.add("not inside");
					}
				}

				if (ob1Attrs.get("inside") == null) {
					if (ob2Attrs.get("inside") != null) {
						mutations.add("become inside");
					}
				}

				if (ob1Attrs.get("above") != null) {
					if (ob2Attrs.get("above") == null) {
						mutations.add("not above");
					}
				}

				if (ob1Attrs.get("above") == null) {
					if (ob2Attrs.get("above") != null) {
						mutations.add("become above");
					}
				}

				for (Entry<RavensObject, Boolean> match : matched.entrySet()) {
					if (match.getValue() == false) {
						mutations.add(String.format("%s %s appeared",  match.getKey().getAttributes().get("size"), match.getKey().getAttributes().get("shape")));
						//match.setValue(true);
					}
				}

				tr.setMutations(mutations);
				results.add(tr);
			}
		}

		return results;
	}

	private int getDiff(RavensObject ob1, RavensObject ob2) {
		int diff = 0;

		for (Map.Entry<String, String> myAttr1 : ob1.getAttributes().entrySet()) {
			boolean found = false;
			for (Map.Entry<String, String> myAttr2 : ob2.getAttributes()
					.entrySet()) {

				if (myAttr1.getKey().compareTo(myAttr2.getKey()) == 0) {
					found = true;
					if (myAttr1.getKey().compareTo("angle") != 0
							&& myAttr1.getKey().compareTo("alignment") != 0
							&& myAttr1.getValue().compareTo(myAttr2.getValue()) != 0) {
						diff++;
					}
				}
			}

			if (!found) {
				diff++;
			}
		}

		return diff;
	}
	
	public class Transformation {
	    private RavensObject source;
	    private RavensObject target;
	    ArrayList<String> mutations;

	    public ArrayList<String> getMutations() {
			return mutations;
		}

		public void setMutations(ArrayList<String> mutations) {
			this.mutations = mutations;
		}

		public RavensObject getSource() {
	        return source;
	    }

	    public void setSource(RavensObject source) {
	        this.source = source;
	    }

	    public RavensObject getTarget() {
	        return target;
	    }

	    public void setTarget(RavensObject target) {
	        this.target = target;
	    }

	}

}