import os, glob
import matplotlib.pyplot as plt
import numpy as np

benchmarks = []

benchmarks_soft = []
benchmarks_hard = []

# parsing the data
for filename in glob.glob('*.txt'):
    with open(os.path.join(os.getcwd(), filename), 'r') as benchmark:
        [nbClasses, classes, paramsString, solutions] = benchmark.readlines()
        params = paramsString.split()
        classesList = [int(c) for c in classes.split()]
        problem = [int(nbClasses), classesList, int(params[0]), params[1] == 'true', int(params[2]), int(params[3])]
        
        listSolutions = []
        for stats in solutions.split(";"):
            listSolutions.append([float(x) for x in stats.split()])
        problem.append(listSolutions)

        key = int(nbClasses)*1000 + sum(classesList)
        benchmarks.append((key, problem))

        if problem[3]:
            benchmarks_soft.append((key, problem))
        else:
            benchmarks_hard.append((key, problem))

# best solution / time graphs
times_hard = []
for problemTuple in sorted(benchmarks_hard):
    times_hard.append(problemTuple[1][-1][-1][0])

times_soft = []
for problemTuple in sorted(benchmarks_soft):
    times_soft.append(problemTuple[1][-1][-1][0])

max_students_soft = []
for problemTuple in sorted(benchmarks_soft):
    max_students_soft.append(problemTuple[1][-1][-1][1] / problemTuple[1][4])

max_classes_hard = []
for problemTuple in sorted(benchmarks_hard):
    max_classes_hard.append(problemTuple[1][-1][-1][2] / problemTuple[1][5])

max_classes_soft = []
for problemTuple in sorted(benchmarks_soft):
    max_classes_soft.append(problemTuple[1][-1][-1][2] / problemTuple[1][5])

if (len(times_soft) != len(times_hard)):
    raise ValueError('Not the same number of soft/hard problems!')


def best_solution_plots(hard_data, soft_data, filename, ylim, ylabel, title):
    # Create subplots
    fig, axes = plt.subplots(1, 5, sharey=True)

    # Iterate over the groups and plot the data
    for i, ax in enumerate(axes):
        start_idx = i * 21
        end_idx = (i + 1) * 21
        x_group = np.arange(1, 22)  # Update x-axis range for each group
        if (hard_data != []):
            ax.plot(x_group, hard_data[start_idx:end_idx], 'b')
        ax.plot(x_group, soft_data[start_idx:end_idx], 'g')
        ax.set_xlim(1, 21)  # Set x-axis limits to 1-21
        #ax.set_ylim(0, ylim)

        if i == 0:
            ax.legend(['Meeting the same participant not allowed', 'Meeting the same participant allowed'], loc = 'upper left', bbox_to_anchor=(0, 1.09), ncol=2)  # Add legend only in the first subplot

        # Add a sub-title below the x-axis
        ax.text(0.5, -0.1, f'Problems with {i+3} classes', transform=ax.transAxes,
                horizontalalignment='center', verticalalignment='center')

    # Title and labels
    plt.suptitle(title)
    fig.text(0.95, 0.09, 'Problem number', ha='center', va='center')
    fig.text(0.08, 0.5, ylabel, ha='center', va='center', rotation='vertical')

    # Adjust spacing between subplots
    plt.subplots_adjust(wspace=0.1)

    # Save the plot
    figure = plt.gcf() # get current figure
    figure.set_size_inches(12, 6)
    plt.savefig(filename, dpi = 100)

best_solution_plots(times_hard, times_soft, 'solutions.png', 900, 'Time taken (seconds)', 'Time taken to find the best solution of each problem')
best_solution_plots([], max_students_soft, 'best_max_students.png', 1, 'Maximisation rate of students met', 'Maximisation rate of students met for the best solution of each problem')
best_solution_plots(max_classes_hard, max_classes_soft, 'best_max_classes.png', 1, 'Maximisation rate of students met', 'Maximisation rate of classes met for the best solution of each problem')