## General
- When the error function is quadratic wrt to the parameters, the derivative is linear, hence min(E[w]) has a unique solution which can be found in closed form.
-   **Bias:** extent to which the average prediction over the dataset vary around the average  
    **Variance:** extent to which solutions for individual samples vary around the average  
    Expected loss = bias^2 + variance + noise


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

3.  What is Bayes rate?  
    What does a classifier require in order to achieve this optimal error rate?

    Bayes Error Rate: lowest possible error rate for any clf. It is analogous to irreducible error. Hence it relates to the best possible performance any classifier can achieve.

    Prior Probability: P(x), Posterior Probability: P(x|y)  
    Bayes Theorem unifies both: P(y|x) = P(x|y)p(y) / P(x|y)p(y) + P(x|y')P(y')
    P(y|x) = P(x|y)p(y) / P(x)




## Lecture-04
1. What is the advantage of using non-linear basis functions with a linear model like linear regression?


2.  What is the pseudo-inverse of a matrix? How is it different from the inverse of a matrix?  
    When will the psuedo-inverse and inverse be equivalent?


3. Explain the geometrical interpretation of least squares approach.


4. When can one resort to gradient descent to minimize the objective function?


5.  What happens when the step size is too large in gradient descent?  
    What happens when the step size is too small?


6. What is the difference between gradient descent and stochastic gradient descent?


7.  Gradient descent can always find the global minimum. True or False?  
    If false, is there any scenario when it is guarenteed to find the global minumum?


## Lecture-05
1. What is inductive bias? What is the inductive bias of linear regression and nearest neighbor algorithms?


2. A hypothesis which minimizes the empirical risk is also guarenteed to minimize the true risk. True or
False?


3. Define bias and variance. Explain the bias-variance tradeoff.


4. Define Occam’s razor.


5. Adding regularization controls overfitting. True or False?


6. Can we use L1 regularization and L2 regularization for feature selection? If 
so, explain how will you do
that. Will there be any difference in the feature selection procedure based on whether the reuglarizer
is L1 or L2 regurlarizer?


7. L1 regularization prefers sparse models. Justify.


8. Compare the geometrical views of L1 regularization and L2 regularization and argue why L1 regularizer
sets more weights to zero than L2 regularizer.


## Lecture-06
1. What are the three approaches to solving classification problem? Sort them in ascending order of
procedure complexity.


2. Why are generative models called as generative models?


3. What are linearly separable problems? Give cartoon examples for linearly 
separable 2-class problem
and not linearly separable 2-class problem.


4. In a linear discriminant model, decision surface is perpendicular to the weight vector. Prove.


5. Explain the difference between one-vs-rest classifier and one-vs-one classifier.


6. Explain various ways of solving multi-class classification problem using discriminant functions.
2

## Lecture-07
1. Least-squares solution lacks robustness to outliers when used for classification. Justify.


2. List down the applications of PCA.


3. Why should we constrain the norm of the projection vector in PCA to 1?


4. PCA and LDA project data from one space to another space. How can we use such algorithms for
classification? Which projection will be more helpful to design a classifier and why?


5. Which one of the following projection algorithms is supervised? PCA or LDA?


## Lecture-08
1. What is the difference between a linear model and a generalized linear model?


2. In GDA, covariance matrix of all the class conditional densities are shared. How is this affecting the
decision boundary?


3. Explain the i.i.d assumption.


4. What is the difference between GDA and QDA?


5. Define confusion matrix. How will an ideal matrix look like?


6. Define precision and recall. List two applications where precision is more important and two applications
where recall is more important.


7. Explain the tradeoff between precision and recall.


8. Define F1-measure. What is the advantage of using F1-measure as an evaluation metric?


## Lecture-09
1. Compare GDA and QDA in terms of parameter complexity.


2. Explain the naive Bayes assumption.


3. Gaussian Naive Bayes has linear decision boundary. True or False?


4. What is Laplace smoothing? Why do we need to smooth our Naive Bayes estimates?


5. Laplace smoothing is a biased smoothing. Justify.


6. What are the advantages of discriminative approach over generative approach for classification?


7. Explain the relationship between maximum likelihood and least squares