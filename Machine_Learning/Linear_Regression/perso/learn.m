%% LEARN.M 
% Minimal supervised ML script
% Script will demand:   type of data (csv or space separated)
%                       hypothesis function
%                       problem type (regression or classification)
%                       minimization technique (gradient descent or normal equation)
% Script will output:   optimal theta values


%% GET input
% define data file
dataFile = input('Data File:');
[pathstr,name,ext] = fileparts(dataFile);

if ext == '.txt'
    data = load(dataFile);
elseif ext == '.csv'
    data = csvread(dataFile);
end

% define problem type
%problemType = input('Problem Type: Regression or Classification? [R/C]:');

% define hypothesis
%hypothesis = input('Enter hypothesis coefficients (arbitrary number of coefficients)\nFormat: x_1^a x_2^b ... => [a b ...]');

% define minimization technique
%minimization = input('Minimization technique: Gradient Descent or Normal Equation? [GD/NE]:');

%% DEFINE FEATURES, TARGETS, hypothesis
[m n] = size(data);
n = n-1;

x = data(:,1:n)
y = data(:,n+1)

X = [ones(m,1) x]
h = X*theta