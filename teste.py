import os
import subprocess
import sys


print('\nInstalling application')
sys.stdout.flush()
os.system('gradlew installDebug installDebugAndroidTest')

print('\nInitializing tests')
sys.stdout.flush()
os.system('adb shell am instrument -w com.example.demoappium.test/androidx.test.runner.AndroidJUnitRunner')

process = input("Continue with graph generation? [S/N]")


while process != 'S' and process != 'S' and 'N' and process != 's' and process != 'n':
	process = input("Continue with graph generation? [S/N]")

if (process == 'S' or process == 's'):
	result = subprocess.check_output("adb shell ls sdcard/DemoAppium/screenshots/", shell=True, text=True)
	result_list = result.split()
	result_list.reverse()
	last = result_list[0]

	print('Last test executed: {hour}:{minutes}:{seconds} {day}/{month}/{year}'.format(seconds=last[12:14], minutes=last[10:12], hour=last[8:10], day=last[6:8], month=last[4:6], year=last[0:4]))

	folder = 'sdcard/DemoAppium/screenshots/' + last;

	os.system('adb pull ' + folder + ' .')

	print('Generating graph')
	sys.stdout.flush()
	#os.system('cd last && dot -Tpng -O flow.dot')
	p = subprocess.Popen('dot -Tpng -O flow.dot', cwd=last)
	p.wait()
	print('Graph generated')

else:
	print('Process interrupted')