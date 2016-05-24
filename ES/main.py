from EvolutionaryStrategy import EvolutionaryStrategy
import numpy
import csv



def fit_func(tar_vars):
    fitness = 0.0
    for item in tar_vars:
        fitness += item*item#math.sqrt(item)
    return fitness



#model: f(x1,x2,x3,...,xn)=x1*x1+x2*x2+x3*x3+....+xn
vars_len = 5
bound = numpy.tile([-1000, 1000], (vars_len, 1))
pop_sizes = [5, 10,  15, 20, 25, 30, 35, 40, 45, 50]
selection = [1, 0]
gens=[50, 100, 150, 200, 250, 300, 350, 400, 450, 500]
mutation = [1, 0]


with open('result.csv', 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['selection', 'mutation', 'pop_size', 'gens', 'time', 'best_pop'])
    for select_option in selection:
        for mutation_option in mutation:
            for pop_size in pop_sizes:
                for MAX_GEN in gens:

                    es = EvolutionaryStrategy(pop_size=pop_size, vars_len=vars_len, init_SD=3.0,
                    bound=bound, MAX_GEN=MAX_GEN, fit_func=fit_func,
                    mutation_option=mutation_option, select_option=select_option)
                    es.run()
                    content = list()
                    content.append('elitist' if select_option == 1 else 'offspring')
                    content.append('0.2' if mutation_option == 1 else 'adaptive')
                    content.append(str(pop_size))
                    content.append(str(MAX_GEN))
                    content.append(str(es.duration))
                    content.append(str(es.population[0].fitness))
                    writer.writerow(content)
print('done')








