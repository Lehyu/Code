from ESIndividual import ESIndividual
import numpy
import random
import time

class EvolutionaryStrategy:
    """
    fit_func is the function to calculate fitness of each individual
    pop_size if the size of population
    vars_len is the target vars' len
    select is the function to select population
    bound is the boundaries of each weight of individual's tar_vars
    MAX_GEN is the termination condition
    gen_num is the num of children each generation
    """
    def __init__(self, pop_size, ni, no, vars_len, bound, data, init_SD, MAX_GEN, SD=1, delta=1):
        self.ni = ni
        self.no = no
        self.data = data
        self.init_SD = init_SD
        self.bound = bound
        self.pop_size = pop_size
        self.vars_len = vars_len
        self.MAX_GEN = MAX_GEN
        self.gen_num = pop_size*2
        self.population = []
        self.SD = SD
        self.delta = delta
        self.start = time.time()
        self.duration = 0

    def init_pop(self):
        pop = self.generate()
        for index in range(0, self.pop_size):
            newpop = pop.generate()
            self.population.append(newpop)
            #print('gen pop vars:', newpop.tar_vars)

    def generate(self):
        length = self.vars_len
        rnd = numpy.random.random(size=length)
        tar_vars = []
        steps = [self.init_SD for i in range(0, length)]
        for index in range(0, length):
            var = rnd[index]*self.bound[index][1] + (1 - rnd[index]) * self.bound[index][0]
            while var > self.bound[index][1]\
                or var < self.bound[index][0]:
                var = rnd[index] * self.bound[index][1] + (1 - rnd[index]) * self.bound[index][0]
            tar_vars.append(var)
        pop = ESIndividual(self.ni, self.no, tar_vars, steps, self.bound, self.SD, self.delta)
        #print('init pop:', pop.tar_vars)
        #print('init pop:', pop.steps)
        return pop

    def recombination(self):
        male, female = random.sample(self.population, 2)
        tar_vars = []
        for a,b in zip(male.tar_vars, female.tar_vars):
            tar_vars.append(random.sample([a,b],1)[0])
        steps = []
        for a,b in zip(male.steps, female.steps):
            steps.append(random.sample([a,b],1)[0])
        return ESIndividual(self.ni, self.no, tar_vars, steps, self.bound, self.SD, self.delta)

    def run(self):
        self.init_pop()
        for index in range(0, self.MAX_GEN):
            children = []
            for k in range(0, self.gen_num):
                child = self.recombination()
                # print('before mutate:', child.tar_vars)
                child.run(self.data)
                #  print('after mutate:', child.tar_vars)
                #   print('')
                children.append(child)
            # print('before selection: ', self.population)
            self.elitist_selection(self.population, children)
        self.duration = time.time() - self.start


    def elitist_selection(self, parents, children):
        total_pop = parents
        total_pop.extend(children)
        #print('before sort:', total_pop)
        total_pop.sort(key=lambda pop:pop.fitness, reverse=True)
        #sorted(total_pop, key=lambda pop:pop.fitness)
        #print('after sort:', total_pop)
        self.population = total_pop[:self.pop_size]

    def print(self):
        for pop in self.population:
            print(pop.fitness, '')
