import sys
import math
class rpn_calc:
	def __init__(self):
		self._stack = []
	
	def print_help(self):
		print "The supported operations are:"
		print "\t+ y+x"	
		print "\t- y-x"	
		print "\t* y*x"	
		print "\t/ y/x"	
		print "\t^ y^x"	
		print "\tD pop the stack"
		math_methods = dir(math)
		for meth in math_methods:
			if meth[0] != '_':
				print "\t" + meth
		print "\t? Display this message"	
	def print_stack(self):
		stk_entry = ord("Z")
		print "--------------------"
		for i_stack in range(-3, 0, 1):
			if len(self._stack) >= -1*i_stack:
				print unichr(stk_entry) + ":\t" + str(self._stack[i_stack])
			else:
				print unichr(stk_entry) + ":\t"
			stk_entry = stk_entry - 1
	
		print "--------------------"

	def perform_operation(self, op):
		#There is almost certainly a better way to do this.  Possibly using dicts, I dunno

		#Check if there are enough values on the stack to perform the operation type requested
		if len(self._stack) > 1:
			#Unary/Binary operations
			if op[0] == "+":
				self.perform_addition()
			elif op[0] == "-":
				self.perform_subtraction()
			elif op[0] == "*":
				self.perform_multiplication()
			elif op[0] == "/":
				self.perform_division()
			elif op[0] == "^":
				self.perform_exponentiation()
			else:
				self.perform_other_operation(op)
		else:
			if len(self._stack) > 0 and not( op[0] == "+" or op[0] == "-" or op[0] == "*" or op[0] == "/" or op[0] == "^" ):
				#Unary operations
				self.perform_other_operation(op)
			elif op[0] == "?":
				self.print_help()
			else:
				print "not enlough data on the stack"

	def perform_addition(self):
		x = self._stack.pop()
		y = self._stack.pop()
		self._stack.append(x+y)
		
	def perform_subtraction(self):
		x = self._stack.pop()
		y = self._stack.pop()
		self._stack.append(y-x)

	def perform_multiplication(self):
		x = self._stack.pop()
		y = self._stack.pop()
		self._stack.append(x*y)

	def perform_division(self):
		x = self._stack.pop()
		y = self._stack.pop()
		self._stack.append(y/x)
	
	def perform_exponentiation(self):
		x = self._stack.pop()
		y = self._stack.pop()
		self._stack.append(math.pow(y, x))

	def perform_other_operation(self, op):
		x = self._stack.pop()
		
		try:
			#If the method is defined in math eval it and append the result to the stack
			self._stack.append(eval("math." + op + "(x)"))
		except Exception as e:
			if op[0].upper() == "D":
				#Don't do anything drop the lowest item on the stack
				x = 0
			elif op[0] == "?":
				self.print_help()				
			else:
				#The operation isn't supported, so put the old value back on the stack.
				print "Unknown Operation"
				self._stack.append(x)
			

	def parse_input(self, line):
		line = line.rstrip()

		ops = line.split(" ")

		for op in ops:

			try:
				#If the input op is a number this will succeed
				val = float(op)
				self._stack.append(val)
			except ValueError:
				#The input op wasn't a number
				try:
					val = eval("math." + op)
					try:
						#If the input op is a constant defined in math this will succeed
						#otherwise the conversion to a float of a method defined in math
						#will throw a TypeError
						constant = float(val)
						self._stack.append(constant)
					except TypeError:						
						#The op is a method defined in math
						self.perform_operation(op)

				except Exception as e:
					#The op wasn't defined in math so it is either one of the ops the 
					#program allows, or an unknown operation, but that is up to
					#the operation handler to figure out
					self.perform_operation(op)

	def run_calc(self):
		while True:
			self.parse_input(sys.stdin.readline())
			self.print_stack()
	
	
	
	
	
	
	
