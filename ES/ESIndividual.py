import math
import random


class ESIndividual:
    """
    tar_vars is a list of target var
    steps is a list of standard deviation
    SD is a var of standard deviation
    delta is a var of S.D of each weight of tar_vars
    """
    def __init__(self, tar_vars, steps, bound, mutation_option, SD, delta, fit_func):
        self.tar_vars = tar_vars
        self.steps = steps
        self.bound = bound
        self.mutation_option = mutation_option
        self.SD = SD
        self.delta = delta
        self.fit_func = fit_func
        self.fitness = 0

    def mutate_tar_vars(self):
        N = random.gauss
        for key, item in enumerate(self.tar_vars):
            self.tar_vars[key] = item + N(0, self.steps[key])
            while self.tar_vars[key] < self.bound[key][0] \
                    or self.tar_vars[key] > self.bound[key][1]:
                self.tar_vars[key] = item + N(0, self.steps[key])

    def mutate_adaptive(self):
        N = random.gauss
        for key, item in enumerate(self.steps):
            self.steps[key] = item*math.exp(N(0, self.SD) + N(0, self.delta))

    def mutate5(self, rate):
        for key, item in enumerate(self.steps):
            step = item
            if rate > 0.2:
                step *= 1.22
            elif rate < 0.2:
                step *= 0.82
            self.steps[key] = step

    def generate(self):
        N = random.gauss
        mutate_steps = [item * math.exp(N(0, self.SD) + N(0, self.delta)) for item in self.steps]

        mutate_vars = []
        for key, item in enumerate(self.tar_vars):
            var = item + N(0, self.steps[key])
            while var < self.bound[key][0] \
                    or var > self.bound[key][1]:
                var = item + N(0, self.steps[key])
            mutate_vars.append(var)
        return ESIndividual(mutate_vars, mutate_steps, self.bound, self.mutation_option, self.SD, self.delta, self.fit_func)

    def calculateFintness(self):
        self.fitness = self.fit_func(self.tar_vars)

    def run(self, rate=0.2):
        self.mutate_tar_vars()
        if self.mutation_option == 1:
            self.mutate5(rate)
        else:
            self.mutate_adaptive()
        self.calculateFintness()

    def __repr__(self):
        return repr(self.fitness)

