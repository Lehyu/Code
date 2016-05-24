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
    def __init__(self, pop_size, vars_len, init_SD, bound, MAX_GEN, fit_func, mutation_option =1, select_option=1, SD=1, delta=1):
        self.pop_size = pop_size
        self.vars_len = vars_len
        self.bound = bound
        self.MAX_GEN = MAX_GEN
        self.gen_num = pop_size*7
        self.fit_func = fit_func
        self.select_option = select_option
        self.population = []
        self.SD = SD
        self.init_SD = init_SD
        self.delta = delta
        self.mutation_option = mutation_option
        self.start = time.time()
        self.duration = 0

    def init_pop(self):
        pop = self.generate()
        for index in range(0, self.pop_size):
            newpop = pop.generate()
            newpop.calculateFintness()
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
                var = rnd[index]*self.bound[index][1] + (1 - rnd[index]) * self.bound[index][0]
            tar_vars.append(var)
        pop = ESIndividual(tar_vars, steps, self.bound, self.mutation_option, self.SD, self.delta, self.fit_func)
        #print('init pop:', pop.tar_vars)
        #print('init pop:', pop.steps)
        return pop

    def recombination(self):
        male, female = random.sample(self.population,2)
        tar_vars = []
        for a,b in zip(male.tar_vars, female.tar_vars):
            tar_vars.append(random.sample([a,b],1)[0])
        steps = []
        for a,b in zip(male.steps, female.steps):
            steps.append(random.sample([a,b],1)[0])
        return ESIndividual(tar_vars, steps, self.bound, self.mutation_option, self.SD, self.delta, self.fit_func)

    def run(self):
        self.init_pop()
        if self.mutation_option == 1:
            self.run5()
        else:
            self.run_adaptive()
        self.duration = time.time() - self.start


    def run5(self):
        w = 0
        success = 0

        for index in range(0, self.MAX_GEN):
            rate = 0.2
            #print('success: ', success)
            if w == 10:
                rate = success/w
                w = 0
                success = 0
            w+=1
            children = []
            #print(rate)
            for k in range(0, self.gen_num):
                child = self.recombination()
                child.run(rate)
                children.append(child)
            # print('before selection: ', self.population)
            tmp = list(self.population)
            if self.select_option == 1:
                self.elitist_selection(self.population, children)
            else:
                self.offspring_selection(children)
                # print('after selection: ', self.population)
            for key, item in enumerate(self.population):
                if item.fitness > (tmp[key]).fitness:
                    success += 1
                    break



    def run_adaptive(self):
        for index in range(0, self.MAX_GEN):
            children = []
            for k in range(0, self.gen_num):
                child = self.recombination()
                # print('before mutate:', child.tar_vars)
                child.run()
                #  print('after mutate:', child.tar_vars)
                #   print('')
                children.append(child)
            # print('before selection: ', self.population)
            if self.select_option == 1:
                self.elitist_selection(self.population, children)
            else:
                self.offspring_selection(children)
                # print('after selection: ', self.population)

    def elitist_selection(self, parents, children):
        total_pop = parents
        total_pop.extend(children)
        #print('before sort:', total_pop)
        total_pop.sort(key=lambda pop:pop.fitness, reverse=True)
        #sorted(total_pop, key=lambda pop:pop.fitness)
        #print('after sort:', total_pop)
        self.population = total_pop[:self.pop_size]

    def offspring_selection(self, children):
        children.sort(key=lambda pop:pop.fitness, reverse=True)
        self.population = children[:self.pop_size]

    def print(self):
        for pop in self.population:
            print(pop.fitness, '')
