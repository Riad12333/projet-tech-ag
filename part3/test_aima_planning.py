import sys
import os
import io
import re
import tkinter as tk
from tkinter import scrolledtext, ttk
import matplotlib
matplotlib.use('TkAgg')  # Use TkAgg backend to embed inside Tkinter
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import networkx as nx

# Add aima-python to path
sys.path.append(os.path.join(os.path.dirname(__file__), 'aima-python'))

try:
    from planning import PlanningProblem, Action, expr  # type: ignore
    from planning import PartialOrderPlanner  # type: ignore
except ImportError as e:
    print("Error importing aima-python modules. Make sure it is cloned properly in part3/aima-python.")
    print(e)
    sys.exit(1)

# ─── Planning Problem Definitions ───────────────────────────────────────────

def get_shoes_socks_problem():
    """
    Classic POP demonstration problem.
    Partial Order allows putting on left & right socks/shoes in any valid order.
    """
    description = (
        "Initial state: All socks and shoes are off.\n"
        "Goal:          Both shoes are on.\n"
        "Key insight:   Left and right foot actions are INDEPENDENT → parallel partial order.\n"
    )
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
    return PlanningProblem(initial=init, goals=goal, actions=actions), "Shoes & Socks", description


def get_spare_tire_problem():
    """
    AIMA Chapter 11 classic: replacing a flat tire.
    Actions must be sequenced strictly: remove flat THEN remove spare THEN mount spare.
    """
    description = (
        "Initial state: Flat tire on axle, spare tire in trunk.\n"
        "Goal:          Spare tire is mounted on the axle.\n"
        "Key insight:   Strict sequential ordering — axle must be cleared before mounting spare.\n"
    )
    init = [expr('AtFlatAxle'), expr('AtSpareTrunk')]
    goal = [expr('AtSpareAxle')]
    actions = [
        Action(expr('RemoveFlatAxle'),
               precond=[expr('AtFlatAxle')],
               effect=[expr('FlatGround'), expr('~AtFlatAxle')]),
        Action(expr('RemoveSpareTrunk'),
               precond=[expr('AtSpareTrunk')],
               effect=[expr('SpareGround'), expr('~AtSpareTrunk')]),
        Action(expr('PutOnSpareAxle'),
               precond=[expr('SpareGround'), expr('~AtFlatAxle')],
               effect=[expr('AtSpareAxle'), expr('~SpareGround')])
    ]
    return PlanningProblem(initial=init, goals=goal, actions=actions), "Spare Tire Problem", description


def get_have_cake_eat_cake_problem():
    """
    AIMA Chapter 11 classic: you want to both have and eat the cake.
    Requires baking a new cake after eating.
    """
    description = (
        "Initial state: Agent has one cake.\n"
        "Goal:          Agent has a cake AND has eaten a cake.\n"
        "Key insight:   Eating destroys the cake → baking a new one is required after eating.\n"
    )
    init = [expr('HaveCake')]
    goal = [expr('HaveCake'), expr('EatenCake')]
    actions = [
        Action(expr('EatCake'),
               precond=[expr('HaveCake')],
               effect=[expr('EatenCake'), expr('~HaveCake')]),
        Action(expr('BakeCake'),
               precond=[expr('~HaveCake')],
               effect=[expr('HaveCake')])
    ]
    return PlanningProblem(initial=init, goals=goal, actions=actions), "Have Cake & Eat It Too", description


def get_package_delivery_problem():
    """
    Custom real-world inspired problem: a robot must pack and deliver a parcel.
    Demonstrates a richer chain of dependencies with more actions.
    """
    description = (
        "Initial state: Package unpacked, robot at warehouse, vehicle fueled.\n"
        "Goal:          Package is delivered to customer.\n"
        "Key insight:   Multi-step chain — pack → load → drive → unload → deliver.\n"
    )
    init = [
        expr('PackageUnpacked'), expr('RobotAtWarehouse'),
        expr('VehicleFueled'), expr('VehicleAtWarehouse')
    ]
    goal = [expr('PackageDelivered')]
    actions = [
        Action(expr('PackParcel'),
               precond=[expr('PackageUnpacked'), expr('RobotAtWarehouse')],
               effect=[expr('PackageReady'), expr('~PackageUnpacked')]),
        Action(expr('LoadVehicle'),
               precond=[expr('PackageReady'), expr('VehicleAtWarehouse')],
               effect=[expr('PackageInVehicle'), expr('~PackageReady')]),
        Action(expr('DriveToCustomer'),
               precond=[expr('VehicleFueled'), expr('VehicleAtWarehouse')],
               effect=[expr('VehicleAtCustomer'), expr('~VehicleAtWarehouse')]),
        Action(expr('UnloadVehicle'),
               precond=[expr('PackageInVehicle'), expr('VehicleAtCustomer')],
               effect=[expr('PackageAtDoor'), expr('~PackageInVehicle')]),
        Action(expr('HandOver'),
               precond=[expr('PackageAtDoor')],
               effect=[expr('PackageDelivered'), expr('~PackageAtDoor')])
    ]
    return PlanningProblem(initial=init, goals=goal, actions=actions), "Package Delivery", description


class ModernPlannerDashboard:
    def __init__(self, root):
        self.root = root
        self.root.title("AIMA POP Planner Dashboard")
        self.root.geometry("1100x680")
        self.root.configure(bg="#F4F6F9")  # Clean, minimal light-grey background
        self.root.resizable(True, True)

        # Style Palette (Minimal & Modern Light Theme)
        self.bg_light = "#F4F6F9"
        self.bg_card = "#FFFFFF"
        self.accent_blue = "#007AFF"  # Flat clean blue
        self.text_primary = "#2D3748"  # Slate dark text
        self.text_muted = "#718096"
        self.border_color = "#E2E8F0"

        self.setup_ui()

    def setup_ui(self):
        # 1. Minimal Header Banner
        header = tk.Frame(self.root, bg="#FFFFFF", height=75, bd=0, highlightbackground=self.border_color, highlightthickness=1)
        header.pack(fill="x", side="top")
        header.pack_propagate(False)

        title_frame = tk.Frame(header, bg="#FFFFFF")
        title_frame.pack(side="left", padx=25, pady=12)

        title = tk.Label(title_frame, text="AIMA Planning Control Center", font=("Segoe UI", 15, "bold"), fg=self.text_primary, bg="#FFFFFF")
        title.pack(anchor="w")

        subtitle = tk.Label(title_frame, text="Partial Order Planner (POP) Simulation", font=("Segoe UI", 9), fg=self.text_muted, bg="#FFFFFF")
        subtitle.pack(anchor="w", pady=(1, 0))

        # 2. Main Space Container
        workspace = tk.Frame(self.root, bg=self.bg_light)
        workspace.pack(fill="both", expand=True, padx=20, pady=20)

        # Left Column (Controls & Scrolled Text Console)
        left_col = tk.Frame(workspace, bg=self.bg_light, width=440)
        left_col.pack(fill="both", side="left", expand=False, padx=(0, 15))

        # Problem Selector Card
        selector_card = tk.Frame(left_col, bg=self.bg_card, bd=0, highlightbackground=self.border_color, highlightthickness=1)
        selector_card.pack(fill="x", side="top", pady=(0, 15))

        sel_title = tk.Label(selector_card, text="Select Planning Scenario", font=("Segoe UI", 10, "bold"), fg=self.text_primary, bg=self.bg_card)
        sel_title.pack(anchor="w", padx=15, pady=(15, 8))

        # Control Group (Combobox & Button side by side inside a frame)
        control_group = tk.Frame(selector_card, bg=self.bg_card)
        control_group.pack(fill="x", padx=15, pady=(0, 15))

        self.problem_var = tk.StringVar(value="Shoes and Socks")
        problems = ["Shoes and Socks", "Spare Tire Problem", "Have Cake & Eat It Too", "Package Delivery"]
        
        # Style the Combobox elegantly
        style = ttk.Style()
        style.theme_use('clam')
        style.configure('TCombobox', 
                        fieldbackground="#FFFFFF", 
                        background="#F8FAFC", 
                        foreground=self.text_primary, 
                        bordercolor=self.border_color, 
                        lightcolor=self.border_color, 
                        darkcolor=self.border_color,
                        font=("Segoe UI", 10))
        
        self.dropdown = ttk.Combobox(control_group, textvariable=self.problem_var, values=problems, state="readonly", width=22)
        self.dropdown.pack(side="left", padx=(0, 10), ipady=3)

        self.btn_run = tk.Button(control_group, text="Run Planner", font=("Segoe UI", 9, "bold"), 
                                 bg=self.accent_blue, fg="white", activebackground="#005ECB", activeforeground="white",
                                 bd=0, cursor="hand2", padx=18)
        self.btn_run.pack(side="left", fill="y", ipady=2)
        
        # Hover Animations
        self.btn_run.bind("<Enter>", lambda e: self.btn_run.configure(bg="#005ECB"))
        self.btn_run.bind("<Leave>", lambda e: self.btn_run.configure(bg=self.accent_blue))
        self.btn_run.configure(command=self.run_planning)

        # Scrolled Text terminal card
        term_card = tk.Frame(left_col, bg=self.bg_card, bd=0, highlightbackground=self.border_color, highlightthickness=1)
        term_card.pack(fill="both", side="top", expand=True)

        term_title = tk.Label(term_card, text="Console Output Log", font=("Segoe UI", 10, "bold"), fg=self.text_primary, bg=self.bg_card)
        term_title.pack(anchor="w", padx=15, pady=(15, 8))

        # Terminal scroll widget styled in minimal light grey
        self.console = scrolledtext.ScrolledText(term_card, wrap=tk.WORD, bg="#F8FAFC", fg="#334155", 
                                                insertbackground="black", font=("Consolas", 10), bd=0,
                                                highlightthickness=1, highlightbackground=self.border_color)
        self.console.pack(fill="both", expand=True, padx=15, pady=(0, 15))
        self.console.insert(tk.END, "Ready...\nSelect a scenario above and click 'Run Planner'.\n")

        # Right Column (Matplotlib canvas)
        self.right_col = tk.Frame(workspace, bg=self.bg_card, bd=0, highlightbackground=self.border_color, highlightthickness=1)
        self.right_col.pack(fill="both", side="right", expand=True)

        right_title = tk.Label(self.right_col, text="Constraints Dependency Graph", font=("Segoe UI", 10, "bold"), fg=self.text_primary, bg=self.bg_card)
        right_title.pack(anchor="w", padx=15, pady=(15, 8))

        self.canvas_widget = None
        self.clear_plot()

    def clear_plot(self):
        if self.canvas_widget:
            self.canvas_widget.destroy()
        
        # Elegant clean light placeholder label
        self.placeholder_lbl = tk.Label(self.right_col, text="Waiting for planning task execution...\nThe temporal ordering constraints diagram will render here.", 
                                        font=("Segoe UI", 10, "italic"), bg=self.bg_card, fg=self.text_muted)
        self.placeholder_lbl.pack(fill="both", expand=True, padx=20, pady=20)

    def run_planning(self):
        # 1. Clean console and UI
        self.console.delete("1.0", tk.END)
        self.console.insert(tk.END, "[System] Initializing plan solver...\n")

        # 2. Get Selected Problem
        selected = self.problem_var.get()
        if selected == "Shoes and Socks":
            problem, title, description = get_shoes_socks_problem()
        elif selected == "Spare Tire Problem":
            problem, title, description = get_spare_tire_problem()
        elif selected == "Have Cake & Eat It Too":
            problem, title, description = get_have_cake_eat_cake_problem()
        else:
            problem, title, description = get_package_delivery_problem()

        # Show scenario description in console
        self.console.insert(tk.END, f"Scenario: {title}\n")
        self.console.insert(tk.END, "─" * 50 + "\n")
        self.console.insert(tk.END, description)
        self.console.insert(tk.END, "─" * 50 + "\n")
        self.console.insert(tk.END, "[POP] Running Partial Order Planner...\n\n")

        # 3. Capture stdout to get AIMA constraints
        captured_output = io.StringIO()
        original_stdout = sys.stdout
        sys.stdout = captured_output

        try:
            planner = PartialOrderPlanner(problem)
            planner.execute()
        except Exception as e:
            sys.stdout = original_stdout
            self.console.insert(tk.END, f"[Error] Execution failed: {e}\n")
            return

        sys.stdout = original_stdout
        plan_text = captured_output.getvalue()

        # ── Parse & display a structured summary ─────────────────────────────
        # Extract actions (lines that are not constraints/header/blank)
        action_lines = []
        in_actions = False
        in_constraints = False
        for line in plan_text.split('\n'):
            stripped = line.strip()
            if stripped.startswith("Partial Order Plan"):
                in_actions = True
                in_constraints = False
                continue
            if stripped.startswith("Constraints"):
                in_constraints = True
                in_actions = False
                continue
            if in_actions and stripped and not stripped.startswith('Constraints'):
                action_lines.append(stripped)

        if action_lines:
            self.console.insert(tk.END, "[Plan Steps Detected]\n")
            for i, step in enumerate(action_lines, 1):
                self.console.insert(tk.END, f"  Step {i}: {step}\n")
            self.console.insert(tk.END, "\n")

        # Also print raw output for Constraints section
        self.console.insert(tk.END, "[Raw Constraints Output]\n")
        self.console.insert(tk.END, plan_text)
        self.console.see(tk.END)

        # 4. Parse output constraints & Build Graphe
        G = nx.DiGraph()
        causal_edges = []
        causal_labels = {}
        
        causal_section = False
        constraints_section = False
        
        for line in plan_text.split('\n'):
            line = line.strip()
            if line.startswith("Causal Links"):
                causal_section = True
                constraints_section = False
                continue
            elif line.startswith("Constraints"):
                constraints_section = True
                causal_section = False
                continue
            elif line.startswith("Partial Order Plan") or not line:
                if constraints_section and not line:
                    continue
                if line.startswith("Partial Order Plan"):
                    constraints_section = False
                    causal_section = False
            
            if constraints_section and '<' in line:
                parts = line.split('<')
                if len(parts) == 2:
                    u = parts[0].strip()
                    v = parts[1].strip()
                    G.add_edge(u, v)
                    
            if causal_section and line.startswith('(') and line.endswith(')'):
                match = re.match(r"\(([^,]+),\s*([^,]+),\s*([^)]+)\)", line)
                if match:
                    u = match.group(1).strip()
                    cond = match.group(2).strip()
                    v = match.group(3).strip()
                    G.add_edge(u, v)
                    causal_edges.append((u, v))
                    causal_labels[(u, v)] = cond

        # 5. Render using Matplotlib inside Tkinter
        if self.placeholder_lbl:
            self.placeholder_lbl.destroy()
            self.placeholder_lbl = None

        if self.canvas_widget:
            self.canvas_widget.destroy()

        # Custom light-themed Matplotlib Figure to blend in perfectly with sleek horizontal dimensions
        fig, ax = plt.subplots(figsize=(9.5, 4.5), facecolor=self.bg_card)
        ax.set_facecolor(self.bg_card)
        ax.axis('off')

        if len(G.nodes) > 0:
            # 1. Custom Left-to-Right Layered Layout for DAG
            start_node = next((n for n in G.nodes() if "start" in n.lower()), None)
            finish_node = next((n for n in G.nodes() if "finish" in n.lower() or "end" in n.lower()), None)
            
            layers = {}
            for node in G.nodes():
                if node == start_node:
                    layers[node] = 0
                else:
                    try:
                        paths = list(nx.all_simple_paths(G, start_node, node))
                        layers[node] = max(len(p) - 1 for p in paths) if paths else 1
                    except:
                        layers[node] = 1
            
            if finish_node:
                max_internal_layer = max((l for n, l in layers.items() if n != finish_node), default=1)
                layers[finish_node] = max_internal_layer + 1

            layer_groups = {}
            for node, l in layers.items():
                layer_groups.setdefault(l, []).append(node)
                
            pos = {}
            max_layer = max(layer_groups.keys()) if layer_groups else 1
            for l, group in sorted(layer_groups.items()):
                x = 0.08 + 0.84 * (l / max_layer)
                num_nodes = len(group)
                for idx, node in enumerate(group):
                    if num_nodes == 1:
                        y = 0.5
                    else:
                        y = 0.25 + 0.5 * (idx / (num_nodes - 1))
                    pos[node] = (x, y)

            # Node styling based on Type (Sleek Circle Nodes)
            node_colors = []
            for node in G.nodes():
                node_lower = node.lower()
                if "start" in node_lower:
                    node_colors.append("#2ECC71")  # Soft Green
                elif "finish" in node_lower or "end" in node_lower:
                    node_colors.append("#E74C3C")  # Soft Red
                else:
                    node_colors.append("#3498DB")  # Soft Blue

            # Draw clean circle nodes
            nx.draw_networkx_nodes(G, pos, ax=ax, node_color=node_colors, node_size=300, 
                                   edgecolors="#FFFFFF", linewidths=1.5)
            
            # Position labels slightly above the node - Clean and readable
            pos_labels = {node: (x, y + 0.07) for node, (x, y) in pos.items()}
            nx.draw_networkx_labels(G, pos_labels, ax=ax, font_size=8, font_family="Segoe UI", 
                                    font_weight='bold', font_color="#2C3E50")
            
            # Partition edges into temporal and causal
            temporal_edges = [edge for edge in G.edges() if edge not in causal_edges]
            
            # Draw temporal constraints (solid grey arrows, straight)
            nx.draw_networkx_edges(G, pos, edgelist=temporal_edges, ax=ax, arrows=True, 
                                   arrowstyle="-|>", arrowsize=10, edge_color="#BDC3C7", 
                                   width=1.0, connectionstyle="arc3,rad=0.0", node_size=300)
            
            # Draw causal links (dashed blue arrows, straight)
            nx.draw_networkx_edges(G, pos, edgelist=causal_edges, ax=ax, arrows=True, 
                                   arrowstyle="-|>", arrowsize=11, edge_color="#2980B9", 
                                   style="dashed", width=1.5, connectionstyle="arc3,rad=0.0", node_size=300)
            
            # Draw edge labels for causal links - clean, borderless floating text
            nx.draw_networkx_edge_labels(G, pos, edge_labels=causal_labels, font_size=7, 
                                         font_family="Segoe UI", font_color='#2980B9', ax=ax, rotate=True,
                                         bbox=dict(facecolor='white', edgecolor='none', alpha=0.85, pad=1))
            
            ax.set_xlim(-0.05, 1.05)
            ax.set_ylim(-0.10, 1.10)
            ax.set_title(f"Constraints Diagram: {title}", fontsize=11, color=self.text_primary, fontweight="bold", pad=15)
        else:
            ax.text(0.5, 0.5, "Empty Plan / Goal Already Achieved", 
                    horizontalalignment='center', verticalalignment='center', color=self.text_muted, fontsize=11)
        
        plt.tight_layout()

        # Pack matplotlib figure inside Tkinter canvas
        canvas = FigureCanvasTkAgg(fig, master=self.right_col)
        self.canvas_widget = canvas.get_tk_widget()
        self.canvas_widget.pack(fill="both", expand=True, padx=15, pady=(0, 15))
        canvas.draw()
        plt.close(fig)

if __name__ == "__main__":
    root = tk.Tk()
    app = ModernPlannerDashboard(root)
    root.mainloop()
