function plotData(x, y)
%PLOTDATA Plots the data points x and y into a new figure 
%   PLOTDATA(x,y) plots the data points and gives the figure axes labels of
%   population and profit.

figure; % open a new figure window

% ====================== YOUR CODE HERE ======================

plot(x,y,'rx','MarkerSize', 3)
xlabel('City Population')
ylabel('Profit')

% ============================================================

end
