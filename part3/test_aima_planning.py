import sys
import os

# Add aima-python to path
sys.path.append(os.path.join(os.path.dirname(__file__), 'aima-python'))

try:
    from planning import PlanningProblem, Action, expr
    from planning import PartialOrderPlanner # Partial Order Planner
except ImportError as e:
    print("Error importing aima-python modules. Make sure it is cloned properly in part3/aima-python.")

    print(e)
    sys.exit(1)

def test_partial_order_planner():
    print("--- Testing AIMA Partial Order Planner ---")
    
    # We will define a simple Shoes & Socks problem
    # Init: RightSockOff, LeftSockOff, RightShoeOff, LeftShoeOff
    # Goal: RightShoeOn, LeftShoeOn
    
    init = [expr('RightSockOff'), expr('LeftSockOff'), expr('RightShoeOff'), expr('LeftShoeOff')]
    goal = [expr('RightShoeOn'), expr('LeftShoeOn')]
    
    actions = [
        Action(expr('PutOnRightSock'), 
               precond=[expr('RightSockOff')], 
               effect=[expr('RightSockOn'), expr('~RightSockOff')]),
        Action(expr('PutOnLeftSock'), 
               precond=[expr('LeftSockOff')], 
               effect=[expr('LeftSockOn'), expr('~LeftSockOff')]),
        Action(expr('PutOnRightShoe'), 
               precond=[expr('RightSockOn'), expr('RightShoeOff')], 
               effect=[expr('RightShoeOn'), expr('~RightShoeOff')]),
        Action(expr('PutOnLeftShoe'), 
               precond=[expr('LeftSockOn'), expr('LeftShoeOff')], 
               effect=[expr('LeftShoeOn'), expr('~LeftShoeOff')])
    ]
    
    shoes_problem = PlanningProblem(initial=init, goals=goal, actions=actions)
    
    print("Problem Defined: Shoes and Socks")
    print("Running Partial Order Planner (POP)...")
    
    try:
        import io
        import sys
        import tkinter as tk
        from tkinter import scrolledtext

        planner = PartialOrderPlanner(shoes_problem)
        
        # Redirect stdout to capture AIMA's print statements
        captured_output = io.StringIO()
        original_stdout = sys.stdout
        sys.stdout = captured_output
        
        planner.execute()
        
        # Restore stdout
        sys.stdout = original_stdout
        
        plan_text = captured_output.getvalue()
        
        # Print to console as well
        print(plan_text)
        print("\nPlan generation complete. Opening Diagram...")
        
        # Parse the captured output to build a graph
        import networkx as nx
        import matplotlib.pyplot as plt
        import re
        
        G = nx.DiGraph()
        
        # Extract Constraints
        constraints_section = False
        for line in plan_text.split('\n'):
            line = line.strip()
            if line.startswith("Constraints"):
                constraints_section = True
                continue
            elif line.startswith("Partial Order Plan") or not line:
                if constraints_section and not line:
                    continue
                if line.startswith("Partial Order Plan"):
                    constraints_section = False
            
            if constraints_section and '<' in line:
                parts = line.split('<')
                if len(parts) == 2:
                    u = parts[0].strip()
                    v = parts[1].strip()
                    G.add_edge(u, v)
        
        # Draw the graph
        plt.figure(figsize=(10, 6))
        
        # Use a topological sort layout if possible, or spring layout
        try:
            pos = nx.nx_pydot.graphviz_layout(G, prog='dot')
        except:
            # Fallback to spring layout
            pos = nx.spring_layout(G, seed=42)
            
        nx.draw(G, pos, with_labels=True, node_color='lightblue', 
                node_size=3000, font_size=10, font_weight='bold', 
                arrows=True, arrowsize=20)
        
        plt.title("Partial Order Plan: Shoes and Socks")
        plt.margins(0.2)
        plt.show()
        
    except Exception as e:
        sys.stdout = original_stdout # Ensure it's restored on error
        print("\nAn error occurred during planning:", e)

if __name__ == "__main__":
    test_partial_order_planner()
