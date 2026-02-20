# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """

    def get_action(self, game_state):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        legal_moves = game_state.getLegalActions()
        scores = [self.evaluation_function(game_state, action) for action in legal_moves]
        best_score = max(scores)
        best_indices = [index for index in range(len(scores)) if scores[index] == best_score]
        chosen_index = random.choice(best_indices) 

        "Add more of your code here if you want to"

        return legal_moves[chosen_index]

    def evaluation_function(self, current_game_state, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (new_food) and Pacman position after moving (new_pos).
        new_scared_times holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        successor_game_state = current_game_state.generatePacmanSuccessor(action)
        new_pos = successor_game_state.getPacmanPosition()
        new_food = successor_game_state.getFood()
        new_ghost_states = successor_game_state.getGhostStates()
        new_scared_times = [ghost.scaredTimer for ghost in new_ghost_states]
        score = successor_game_state.getScore()

        ghost_distances = [
            util.manhattan_distance(new_pos, ghost.getPosition())
            for ghost in new_ghost_states
            if ghost.scaredTimer == 0
        ]

        if ghost_distances:
            closest_ghost_distance = min(ghost_distances)
            if closest_ghost_distance == 0:
                return -999999
            elif closest_ghost_distance < 2:
                return -400
            score -= 10 / closest_ghost_distance

        food_list = new_food.asList()
        if food_list:
            food_distance = min(util.manhattan_distance(new_pos, food) for food in food_list)
            score += 10 / food_distance

        if action == "Stop":
            score -= 100

        return score
def score_evaluation_function(current_game_state):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search game
      (not reflex game).
    """
    return current_game_state.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search game.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """
    

    def __init__(self, evalFn = 'score_evaluation_function', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def get_action(self, game_state):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
            Returns the total number of game in the game
        """
        "*** YOUR CODE HERE ***"
        legalMoves = game_state.getLegalActions(0)
        bestScore = float('-inf')
        bestAction = None
    
        for action in legalMoves:
            successor = game_state.generateSuccessor(0, action)
            score = self.minimax(successor, self.depth, 1)
            if score > bestScore:
                bestScore = score
                bestAction = action
        return bestAction
        util.raise_not_defined()

    def minimax(self, game_state, depth, agentIndex):

        if depth == 0 or game_state.isWin() or game_state.isLose():
            return self.evaluationFunction(game_state)
        
        numAgents = game_state.getNumAgents()
        legalActions = game_state.getLegalActions(agentIndex)
        

        if agentIndex == 0:
            bestScore = float('-inf')
            for action in legalActions:
                successor = game_state.generateSuccessor(agentIndex, action)
                score = self.minimax(successor, depth, 1)
                bestScore = max(bestScore, score)
            return bestScore

        else:
            bestScore = float('inf')
            for action in legalActions:
                successor = game_state.generateSuccessor(agentIndex, action)
                if agentIndex == numAgents - 1:
                    score = self.minimax(successor, depth - 1, 0)
                else:
                    score = self.minimax(successor, depth, agentIndex + 1)
                bestScore = min(bestScore, score)
            return bestScore

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def get_action(self, game_state):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        legalMoves = game_state.getLegalActions(0)
        bestScore = float('-inf')
        Alpha = float('-inf')
        BetaAgent = float('inf')
        bestAction = None

        for action in legalMoves:
            successor = game_state.generateSuccessor(0, action)
            score = self.Min_Score(successor, self.depth,Alpha,BetaAgent, 1)
            if score > bestScore:
                bestScore = score
                bestAction = action
            if score > BetaAgent:
                return bestAction
            Alpha = max(Alpha,score)
        return bestAction
    
    def Max_Score(self, game_state, depth,Alpha,BetaAgent,agentIndex):
        if depth == 0 or game_state.isWin() or game_state.isLose():
            return self.evaluationFunction(game_state)

        legalActions = game_state.getLegalActions(agentIndex)
        bestScore = float('-inf')
        for action in legalActions:
            successor = game_state.generateSuccessor(agentIndex, action)
            score = self.Min_Score(successor, depth,Alpha,BetaAgent, 1)
            bestScore = max(bestScore, score)
            if(bestScore > BetaAgent):
                return bestScore
            Alpha = max(Alpha,bestScore)
        return bestScore


    def Min_Score(self,game_state,depth,Alpha,BetaAgent,agentIndex):
                
        if game_state.isWin() or game_state.isLose():
            return self.evaluationFunction(game_state)

        numAgents = game_state.getNumAgents()
        legalActions = game_state.getLegalActions(agentIndex)

        worstScore = float('inf')
        for action in legalActions:
            successor = game_state.generateSuccessor(agentIndex, action)
            if agentIndex == numAgents - 1:
                score = self.Max_Score(successor, depth - 1,Alpha,BetaAgent, 0)
                worstScore = min(worstScore, score)
                if(worstScore < Alpha):
                    return worstScore
                BetaAgent = min(BetaAgent,worstScore)
            else:
                score = self.Min_Score(successor, depth,Alpha,BetaAgent, agentIndex + 1)
                worstScore = min(worstScore, score)
                if(worstScore < Alpha):
                    return worstScore
                BetaAgent = min(BetaAgent,worstScore)
        return worstScore


class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def get_action(self, game_state):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        
        legalMoves = game_state.getLegalActions(0)
        bestScore = float('-inf')
        bestAction = None
            
        for action in legalMoves:
            successor = game_state.generateSuccessor(0, action)
            score = self.expectValue(successor, self.depth, 1)
            if score > bestScore:
                bestScore = score
                bestAction = action
        return bestAction

    
    def Max_Score(self, game_state, depth):
        if depth == 0 or game_state.isWin() or game_state.isLose():
            return self.evaluationFunction(game_state)

        legalActions = game_state.getLegalActions(0)
        if not legalActions:
            return self.evaluationFunction(game_state)

        bestScore = float('-inf')
        for action in legalActions:
            successor = game_state.generateSuccessor(0, action)
            score = self.expectValue(successor, depth, 1)
            bestScore = max(bestScore, score)
            
        return bestScore

    def expectValue(self,gameState,depth, agentIndex):
        if gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
        legalActions = gameState.getLegalActions(agentIndex)
        Sum_scores = 0
        for action in legalActions:
            successor= gameState.generateSuccessor(agentIndex,action)
            if agentIndex == (gameState.getNumAgents() - 1):
                score = self.Max_Score(successor,depth-1)
            else:
                score = self.expectValue(successor,depth,agentIndex+1)
            Sum_scores = Sum_scores + score
        if legalActions == 0:
                return  0
        return float(Sum_scores)/float(len(legalActions))


def betterEvaluationFunction(currentGameState):
    
    from util import manhattan_distance
    pos = currentGameState.getPacmanPosition()
    food_list = currentGameState.getFood().asList()
    capsules = currentGameState.getCapsules()
    ghosts = currentGameState.getGhostStates()
    scared_times = [ghost.scaredTimer for ghost in ghosts]
    score = currentGameState.getScore()

    ghost_distances = [
        manhattan_distance(pos, ghost.getPosition())
        for ghost in ghosts
    ]
    if any(scared_time > 0 for scared_time in scared_times):
        for ghost in ghosts:
            if ghost.scaredTimer > 0:
                distance = manhattan_distance(pos, ghost.getPosition())
                if distance <= ghost.scaredTimer:
                    score += 200 / (distance + 1)
    else:
        if ghost_distances:
            closest_ghost = min(ghost_distances)
            if closest_ghost == 0:
                return -float("inf")
            elif closest_ghost < 2:
                score -= 400
            score -= 10 / closest_ghost

    if food_list:
        closest_food = min(manhattan_distance(pos, food) for food in food_list)
        score += 10 / closest_food

    score -= 20 * len(capsules)

    return score

    util.raise_not_defined()

# Abbreviation
better = betterEvaluationFunction

