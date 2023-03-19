import matplotlib.pyplot as plt

# tests made for 4 classes
# constraints considered: playing against different classes, playing against different people, playing 6 games

nb_classes = 5
nb_min_students = 10
nb_max_students = 120

nb_students = list(range(nb_min_students, nb_max_students+1, nb_classes))
print(nb_students)

# this was before we added the constraint "if A plays against B, B plays against A"
# runtimes = [0.118, 0.192, 0.331, 0.606, 0.939, 1.351, 1.761, 2.782, 3.651, 4.903, 6.200, 7.131, 8.279, 9.259, 10.818, 13.635, 14.722, 15.692, 17.546, 18.208, 21.232, 28.677, 27.302]

# get this from TournoiV1.java
runtimes = [0.075, -1.000, 0.247, -1.000, 0.641, -1.000, 1.266, -1.000, 2.161, -1.000, 3.356, -1.000, 4.892, -1.000, 6.958, -1.000, 8.913, -1.000, 9.715, -1.000, 11.864, -1.000, 14.579]

check = (len(runtimes) == len(nb_students))

if check:
    plt.plot(nb_students, runtimes)
    plt.xlabel("Number of students in total")
    plt.ylabel("Runtime to find a solution (s)")
    plt.title("Runtimes to find a solution for students divided evenly among " + str(nb_classes) + " classes")
    plt.show()
else:
    print("you don't have the right number of runtimes for the tests considered. check the 'runtimes' list.")