## General
- When the error function is quadratic wrt to the parameters, the derivative is linear, hence min(E[w]) has a unique solution which can be found in closed form.


## Lecture-01
1.  What is the difference between Regularization and Classification problems?

    **reg:** output is numerical. Regression fn: y* = E[y|x]  
    **clf:** output is categorical. Bayesian clf: y* = argmax P(y|X)

2. Give real life applications for classification and ffor regression.

    **reg:** price of stock, temerature, amout of rain, survival rate  
    **clf:** any type of T/F problem (has cancer, is fraud, passes class)

3. When would you say that a particular model is a linear model?

    When the prediction model is linear wrt to the parameters.

4. What is the significance of w0 (bias) in the linear model: y = w0 + w1x?

    Simply learning the parameters wouldn't allow us to cover the whole lane of possible values.   
    The bias enables predictions passing at other points than the origin.

## Lecture-02
1.  What is overfitting?  
    How can we find if a model is overfitting to a particular dataset?

    A model overfits when it learns to predict too well the training samples, but fails to model the underslying function from where the sampels where generated.   
    It can be detected when the model has a very good training score but very bad testing score.

2. Suggest approaches to solve the overfitting problem.

    Regularization, k-fold validation, reducing the number of features, increasing the number of trainign samples

3.  What is the hyperparameter of a model?  
    How is it different from the parameters of the model?  
    How can we choose these hyperparameters?

    - **Model Parameter:** configuration variable internal to the model whose value can is used to predict target output.  
    *Weights in NN, SV in SVM, coeff in regression.*
    - **Model Hyperparameter:** configuration variable external to the model whose value used in process to help estimate parameters. While doing model selection, we choose the best hyperparameter based on the validation set performance.  
    *Learning rate, k*

4.  What will happen if we choose the best hyperparameter based on training set performance?  
    What will happen if we choose the best hyperparameter based on the test set performance?  
    Do we really need a separate validation set?

    Choosing the hyperparameter based on the trainign set performance will lead to overfitting. As the chosen hyperparam will be the one maximizing the score for the same data from which it learned the parameters.

    Choosing the hyperparameter based on the test set performance will induce a bias into the training score. Any result we obtain will be better than what will be expected of a true unknown sample. Because the hyperaram will be the one maximizing the score for the same data from where we will be scoring our model.

    We absolutely need a third set which will allow us to completely isolate the final testing samples from the trianing process, while allowing us to score the performance of the model under different hyperparameters.

## Lecture-03
1. Compare and contrast least squares approach and nearest neighbor approach in terms of bias and variance.

    - LS is a parametric model. It approximates the conditional expectation E[y|x] given the input vector. It lacks robustness to outliers and doesn't work well with multiple classes.  
    Its inductive bias namely being that there is a linear boundary that can separate the problem's output classes or be fitted  to a continous function. Predictions made by this boundary will not fluctuate. This strong bias is the reason why the model is said to have high bias and low variance.  


    - kNN is a non-parametric which makes no prior assumption of the data's distribution. It relies ony on the distance function and the hyperparameter k. Results obtained with kNN will not be biased but will fluctuate more drastically. Hence the model is said th have low bias and high variance.

2.  In the class, we have seen that if we use squared error loss, then the expected prediction error is minimized by the conditional mean.  
    Explain how nearest neighbor approach and least squares approach are trying to approximate this conditional mean.

1. What is the Bayesian theorem?  
    formula that describes how to update the probabilities of hypothesis given evidence.  

    Say we want to know given B how likely is A to occur:
    - **Posterior**: ```P(A|B)```
    - **Likelihood**: ```P(B|A)```
    - **Evidence**: ```P(B)```
    - **Prior**: ```P(A)```

3.  What is Bayes rate?  
    What does a classifier require in order to achieve this optimal error rate?

    Bayes Error Rate: lowest possible error rate for any clf. It is analogous to irreducible error. Hence it relates to the best possible performance any classifier can achieve.

    Prior Probability: P(x), Posterior Probability: P(x|y)  
    Bayes Theorem unifies both: P(y|x) = P(x|y)p(y) / P(x|y)p(y) + P(x|y')P(y')
    P(y|x) = P(x|y)p(y) / P(x)

    To achieve optimal error it requires infinite training samples.

    w/o ind. assumption: ```P(D,S_1-N) = P(D)P(S_1|D)P(S_2|S_1,D).....```
    w/ ind. assumption: ```P(D,S_1-N) = P(D)P(S_1|D)P(S_2|D).....```

## Lecture-04
1. What is the advantage of using non-linear basis functions with a linear model like linear regression?

    We lift the major constraint of only being able to work on linearly separable datasets, while keeping the low variance and relatively simple implementations of linear models.

2.  What is the pseudo-inverse of a matrix?  
    How is it different from the inverse of a matrix?  
    When will the psuedo-inverse and inverse be equivalent?

    We are not always able to compute a matrix inverse (square non-singular matrix).  
    The pseudo-inverse is a generalization of te inverse. Defined for all matrices but with fewer guaranteed properties.  
    They are the same when A is invertible.  
    It is often used to compute a best fit (LS) solution.

3. Explain the geometrical interpretation of least squares approach.

    It is the projection of the target vector Y onto the columns space of X.  
    Visually, its the plane with minimal euclidian distance between itself and all samples.

4. When can one resort to gradient descent to minimize the objective function?

    Gradent descent is computationally cheaper in the case of very large number of features and samples

5.  What happens when the step size is too large in gradient descent?  
    What happens when the step size is too small?

    alpha too large, we might oscilate around the local maxima. We might also overshoot a global maxima.  
    alpha too small, too much time to converge. Might miss global maxima.


6. What is the difference between gradient descent and stochastic gradient descent?

    Gradient Descent or Batch GD uses the whole dataset to compute the gradient.  
    SDG uses a single sample to compute the gradient


7.  Gradient descent can always find the global minimum. True or False?  
    If false, is there any scenario when it is guarenteed to find the global minumum?

    False, Only in convex error functions (e.g. quadratic) 


## Lecture-05
1.  What is inductive bias?  
    What is the inductive bias of linear regression and nearest neighbor algorithms?

    Set of assumptions a model makes in order to generalize beyond training data.  
    Lin Reg assumes output is linearly related to an independent variable.  

    kNN assumes that the classification of an instance x will be most similar to the clf of other instances closest in some distance metric.


2. A hypothesis which minimizes the empirical risk is also guarenteed to minimize the true risk. True or False?

    **Loss:** measurement of accuracy of prediction
    **True Risk:** expectation of the loss (Can't be computed without distribution unkown)  
    **Empirical Risk:** approximation of true risk. Average of loss over training set

    False. True risk is a constant value determined by the chosen model.


3. Define bias and variance. Explain the bias-variance tradeoff.

    **Bias:** 
        Extent to which the average prediction over the dataset vary around the average.
        Intrinsic error due to inductive bias. 
    **Variance:**  
        Extent to which solutions for individual samples vary around the average.  
        Added error because we only have finit samples.

    **Trade-off:** Very rigid models will have high bias, low variance. Very flexible models will have the opposite. One can't completely minimize both at the same time.
  
    Expected loss = bias^2 + variance + noise

4. Define Occam’s razor.

    Formally: simplicity of a hypothesis is a factor of its truthfulness.  
    Informally: when choosing between the models/hyperparameters which yiels relatively equal metrics, one should choose the simplest one (e.g. least features)  

5. Adding regularization controls overfitting. True or False?

    True: it penalizes very large weights attributed to features which would generally result in very well fitted training samples but badly generalized distributions.

6.  Can we use L1 regularization and L2 regularization for feature selection?  
    Will there be any difference in the feature selection procedure based on whether the reuglarizer is L1 or L2 regurlarizer?

    L1 autoatically performs feature selection as it drives most of the feature weights to zero, retaining only the most meaningfull features.

    L2 can be used for feature selection by normalizing the feature's coefficients and removing those with most negligable weight. 

7. Compare the geometrical views of L1 regularization and L2 regularization and argue why L1 regularizer sets more weights to zero than L2 regularizer.

    `TODO`

## Lecture-06
1.  What are the three approaches to solving classification problem?  
    Sort them in ascending order of procedure complexity.

    **Discriminant-based:** learn function that maps x directly to target labels
    **Discriminative-based:** learn the **CONDITIONAL** probability distribution P(x|y) directly, then use decision theory to determine class
    **Generative models:** learn the **JOINT** probability distribution P(x,y), then use Bayes to find P(y|x)


2. Why are generative models called as generative models?

    Because Generative models use the joint distribution to generate likely pairs (x,y)

4. In a linear discriminant model, decision surface is perpendicular to the weight vector. Prove.

    `TODO`

5. Explain the difference between one-vs-rest classifier and one-vs-one classifier.

    The number of clf you learn strongly correlates to the learned decision boundary.  
    1-vs-all will train N clf for N classses. for class i it will assume i-labels as positive, the rest as negative. This often leads to unbalanced datasets.
    1-vs-1 will train N(N-1)/2 clf.

6. Explain various ways of solving multi-class classification problem using discriminant functions.

    `TODO`

## Lecture-07
1. Least-squares solution lacks robustness to outliers when used for classification. Justify.

    The sum of square error fn (SSE) penalizes predictions that are too correct in that they lay a long way on the correct side of the decision boundary.


2. List down the applications of PCA.

    dimensionality reduction, data compression, feature extraction, data visualisation


3. Why should we constrain the norm of the projection vector in PCA to 1?

    Because if not, maximizing the single direction that captures the maximum variance could be achieved by making the vector infinetively large.


4.  PCA and LDA project data from one space to another space. How can we use such algorithms for classification?  
    Which projection will be more helpful to design a classifier and why?

    `TODO`

5. Which one of the following projection algorithms is supervised? PCA or LDA?

    PCA is unsupervised: there are no labels to train which features are the mos significant.  
    LDA is supervised: we label the output targets in order to maximize the separation between class means and minimize the variance within each class


## Lecture-08
1. What is the difference between a linear model and a generalized linear model?

    A linear model is linear wrt to params and has linear decision boundary 
    A generalized linear model is linear wrt to params but has non-linear boundary


2. In GDA, covariance matrix of all the class conditional densities are shared. How is this affecting the decision boundary?

    `TODO` why does it have to be shared?
    This assumption leads to a linead decision boudary.  
    Non-shared covariances lead to quadratic decision boundaries.

3. Explain the i.i.d assumption.

    Each variable has the same probability distribution and all of them are mutuall independent.

    `TODO` why do we need iid


5. Define confusion matrix. How will an ideal matrix look like?

    Its a compact representation of TP,TN,FP,FN. From it its easier to calculate precision and recall.  
    Ideally it would be [[1 0] [0 1]] meaning only TP and TN.


6. Define precision and recall. List two applications where precision is more important and two applications where recall is more important.

    **Prediction:** Out of all positive predictions, how many are trully positive.  
    Important when is more important to have less FP in trade off having more FN.
    **Recall:** Out of all positive samples, how many are correctly predicted as positive.  
    Important when is more important to have less FN in trade off having more FP


7. Explain the tradeoff between precision and recall.

    A clf always saying yes will have 100%  recall, 0% precision and vice versa.    


8. Define F1-measure. What is the advantage of using F1-measure as an evaluation metric?

    It considers both P and R: 2PR/(P+R)


## Lecture-09
2. Explain the naive Bayes assumption.

   Reduces the complexity of learning Bayesian classifiers by assuming all features are conditionnally independent given y. These assumption reduces the number of parameters to learn to 2n

3. Gaussian Naive Bayes has linear decision boundary. True or False?

    True

4. What is Laplace smoothing? Why do we need to smooth our Naive Bayes estimates?
 
    In the context of maximal likelihood, if there are no sample from the class in the subset, the mean estimate would be zero. To avoid this, we add 1 nd divide by 2 to reduce to a prior probability of 1/2

    `TODO` why is it a bias


6. What are the advantages of discriminative approach over generative approach for classification?

    In the case were the class conditional densities have little effect on posterior probabilities, a badly chosen generative model would underperform face to a discriminative model.  
    In the scenario where we only care about clasification discriminative models areless complex and yiels euqally good results with less parameters to estimate.


7. Explain the relationship between maximum likelihood and least squares

    Minimizing least square error is analogous to maximizing likelihood if the errors belong to a normal distribution

## Lecture-10
1. Is there a closed form solution for logistic regression? If not, why?  
    No, it is a non linear function. But because it is a convex function we can easily apply Gradient Descent to optimize it.
2. What is the difference between gradient descent and Newton-Raphson method?
    - Grad. Desc. tries to find a minimum (```f'(x) = 0```) using the first derivative
    - Newton's method tries to find a point satisfying ```f'(x) = 0``` by approximating ```f'``` with a quadratic function ```g``` and using ```f''```. This implies it has higher requirements on the smootheness of f but it converges faster.

3. Derive Newton-Raphson update rule for least squares problem.  

    ```TODO```
4. Explain the geometric view of gradient descent and Newton-Raphson method
    - Grad. Desc. approximates ```f(x)``` at each iter by a costant step times de slope of the function at that point
    - Newton approximates ```f(x)``` at each iter by a quadratic fn ```g```  around ```x``` and takes the step at the minimum of ```g```
5. Prove that in the absence of regularization, the maximum likelihood training for logistic regression can
exhibit severe overfitting for datasets that are linearly separable.
    
    ```TODO```
6. What are the advantages of generative models over discriminative models?  

    Useful for outlier detection
6. What are the advantages of discriminative model over generative model?  

    Simpler, faster: if its simply to classify no need to find the class conditional densities. Often even better results than generative.
8. What are the advantages of discriminative models over discriminant based models?  

    We still have access to P(C|x) which allows us to:
    - **Minimize risk**: if loss matrix is subject to change we can trivially revise minimum risk through decision theory. We would need to retrain model with discriminant based
    - **Compensating for class priors**: e.g. for when dummy clf can still achieve 99% performance. we can balance distributions
    - **Combining models**
9. What is perceptron error criteria?  

    We want W such that ```W.T.dot(X) * Y > 0``` for all samples
10. What are the issues with the perceptron algorithm?
    - the smaller the gap, the more steps to find it
    - soln found is not unique, nor guaranteed to be optimal
    - if data is not lin. sep. algorithm won't converge
## Lecture-11
1. Define margin of a decision boundary.  

    Perpendicular distance between the decision boundary and the closest sample
2. What are active constraints and inactive constraints in a constrained optimization problem?  

    given t*y(x) >= 1
    Samples for which equality holds -> ACTIVE CONTRAINTS.  
    Else -> INACTIVE CONSTRAINTS.  
    Informally, it means a constraint is said to be active for the closest sample to the decision boundary
3. Argue that there will be at least one active constraint in the max-margin problem and that there will be at least two active constraints when the margin is maximized.  

    This follows directly from setting ```t*y(x) = 1``` for the closest point to the boundary. It ensures there will always be at least 1 active contraint, and when maximized we will have another active constraint on the other side of the boundary.
4. In SVMs, when will you solve the primal problem and when will you solve the dual problem?  

    If N << M -> Solve the Dual (obtain a)  
    If N >> M -> Solve the Primal (obtain w, b)
5. What is the error function that max-margin classifier is trying to minimize?  

    Hinge, Squared Hinge
    Sum of ```E(y*t) + lambda||W||^2```
6. What is the motivation for introducing slack variables in the max-margin optimization problem?  

    To allow non-linearly separable data to be fitted (Overlapping class distirbutions).  
    The slack variable acts as a penalty that increases with distance to the boundary
## Lecture-12
1. Compare the characteristics of squarred error, cross-entropy loss, and hinge loss. How are they trying
to approximate the misclassification error? Would you prefer one loss over the other? If so, why?  

    **Logistic regression error** assumes Bernoulli distribution  
    **MSE loss** assumes Gaussian noice  
    **Hinge loss**: samples away from the desc. bound. contribute nothing to the loss. The remaining samples are the support vectors in the context of SVM
    **Logarithmic loss** leads to better probability estimation at the cost of accuracy
    **Hinge loss** leads to better accuracy and some sparsity at the cost of much less sensitivity regarding probabilities
2. What are the differences between parametric methods and non-parametric methods? Give examples
for both methods.
    - Non Param: inf. # of params dependent on dataset. low bias, high variance. kNN, dual SVM
    - Param:  finite # of params ind of dataset. high bias, low variance. GDA, Log. Reg. Primall SVM
3. Why is K-NN called a lazy classifier?  

    It wont compute the estimate until query time. It simply stores the points
4. What is the advantage of using basis functions?  

    The capacity for linear models to fit non linearly separable data
5. What are the various ways of splitting continuous valued attributes in a decision tree?  

    Discretization and Binary Decision
6. What are the different measures of node impurity?  

    Gini Index, Entropy, Misclf Error
7. What is GINI index? How would you compute GINI index for a continuous valued attribute?  

    for a given node t: ```GINI(t) = 1 - sum[P(j|t)]^2``` 
    ```P(j|t)``` being the relative frequency of class j at node t.  
    For continous values: use binary decision based on one value.

8. What is the disadvantage of using entropy as a criteria for attribute test selection in a decision tree?
    Tends to prefer splits that result in large # of partitions each small but pure. It can be avoided with a **Gain Ratio:** Adjusts the info gain by te entropy of the partitioning. Higher entropy partitioning is penalized.
10. Explain reduced error pruning and rule post pruning. What are the advantages of rule post pruning over reduced error pruning?
    - **Reduced Error Prunning:** Nodes are rm only if resulting tree performs no worse than original over validation set.
    - **Rule Post Prunning:** allow tree to grow as large as needed. Obtain set of rules that form each path. Prune each rule by rm preconditions that improve accuracy.Sort rules by accuracy, consider them in this order.
11. What are the advantages of a decision tree classifier?
    Inexpensive, fast, easy to interpret
## Lecture-13
1. What is a mixture of expert? 

    Set of different learners that cover different input regions
2. Explain the procedure for bootstrapping datasets. Why is it useful?  

    Create new datasets by sampling with repetition at random the original dataset. This maintains the same bias but reduces variance.  
    Useful because we can reduce the avg error of a model by a factor of M simply by averaging M versions of the model. The error reduction is a direct result of the variance reduction.
3. Prove that bagging reduces variance when we bag classifiers whose errors are uncorrelated.
    If we assume errors have zero mean and are uncorrelated:
    ```E_comittee = 1/M E_avg```.  
    Note ```E_comittee <= E_avg```
4. What is the difference between bagging decision trees and random forests? Which one would you prefer
and why?  

    Random forests are collections of DT with an emphasiz on preventing OF by creating random subsets of the features and building shaloww trees. finally combining them into a final tree
5. What are the differences between bagging and boosting?  

    Bagging (//) reduces error by reducing the variance in a model.
    Boosting (serial) reduces error by reducing bias in a model. using a sequence of weak learners to correctly predict the previous lerner's misclassified samples
6. What is the error function that AdaBoost is trying to minimize?  

    J = sum[ W I(y(x) != t)]

## Lecture-14
1. What is the optimal solution for exponential error function? How is AdaBoost trying to approximate
it?

    ```TODO```

2. What is k-fold cross validation? What are the advantages and disadvantages of doing k-fold cross
validation?  

    Partitioning the Training set into k bins. use the union of k-1 for training, and 1 for validation. Final accuracy is avg of obtained accuracies
3. Explain stacking.  

    // ensemble method. Use output of multiple families of clf models as input to a meta-model. Uses cross validation performance to give more weight to good generalization models
4. What is representation learning?  

    Learning the basis function for a NN
5. Can we add multiple linear layers in a neural network? If not, why?  

    No, their combination is jsut another linear layer
6. What type of output activation function will you use for the following scenarios:  

 - When the output is a regression target -> identity fn
 - When the output is a probability value -> [0,1] sigmoig fn
 - When the output is a probability distribution -> softmax
7. State universal approximation theorem. 

    A network with a single layer is sufficient to represent any function, but the layer may be infeasibly large and may fail to learn and generalize correctly.
8. What is the advantage of adding more hidden layers in a neural network? 

    Reduce the size of each layer while modeling more complex fns