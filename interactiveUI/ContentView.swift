//
//  ContentView.swift
//  interactiveUI
//
//  Created by Scholar on 7/13/23.
//

import SwiftUI
 
struct ContentView: View {
    
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var textTitle = "What is your name?"
    @State private var age = ""
    @State private var ageTitle = "Enter your age."
    @State private var presentAlert = false
    
    var body: some View {
        
        VStack {
            //this section displayed the name text view, the name input and then the button to display the first and last name at the top
            Text(textTitle)
                .font(.title)
            
            TextField("First name", text: $firstName)
                .multilineTextAlignment(.center)
                .font(.title)
                .border(Color.gray, width: 1)
            
            TextField("Last name", text: $lastName)
                .multilineTextAlignment(.center)
                .font(.title)
                .border(Color.gray, width: 1)
            
            Button("Submit Name"){
                textTitle = "Welcome, \(firstName) \(lastName)"
            }
            .font(.title2)
            .buttonStyle(.borderedProminent)
            .tint(Color(hue: 0.629, saturation: 0.601, brightness: 0.904))
            .padding(.bottom, 150.0)
            
            //this section displayed the button text view then the user age input and then the button that displayed the age text
            Text(ageTitle)
                .font(.title)
            
            TextField("Age", text: $age)
                .multilineTextAlignment(.center)
                .font(.title)
                .border(Color.gray, width: 1)
            
            Button("Submit Age"){
                ageTitle = "You are \(age) years old"
            }
            .font(.title2)
            .buttonStyle(.borderedProminent)
            .tint(Color(hue: 0.629, saturation: 0.601, brightness: 0.904))
            
            //this section is coding for the alert or notification
            Text(presentAlert ? "Presenting": "Dismissed")
            
            Button("Alert") {
                presentAlert = true
            }
            
        }
    }
        //.padding()
}



struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
